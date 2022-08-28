package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotation.RequestMethod;
import com.maxsavteam.ciconia.annotation.handler.RequestContext;
import com.maxsavteam.ciconia.annotation.handler.ParamHandler;
import com.maxsavteam.ciconia.annotation.handler.ParameterAnnotationHandler;
import com.maxsavteam.ciconia.annotation.handler.PathVariableHandler;
import com.maxsavteam.ciconia.component.Controller;
import com.maxsavteam.ciconia.component.ExecutableMethod;
import com.maxsavteam.ciconia.component.ObjectsDatabase;
import com.maxsavteam.ciconia.converter.Converter;
import com.maxsavteam.ciconia.exception.ExecutionException;
import com.maxsavteam.ciconia.exception.MethodNotFoundException;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CiconiaHandler {

	public static final Object VOID = new Object();
	public static final Object ASYNC_METHOD = new Object();

	private static CiconiaHandler instance;

	public static CiconiaHandler getInstance() {
		return instance;
	}

	private static final List<ParameterAnnotationHandler> defaultHandlers = List.of(
			new ParamHandler(),
			new PathVariableHandler()
	);

	private final MappingsContainer mappingsContainer;
	private final ObjectsDatabase objectsDatabase;
	private final CiconiaConfiguration configuration;

	private final List<ParameterAnnotationHandler> handlers = new ArrayList<>();

	private CiconiaHandler(MappingsContainer mappingsContainer, ObjectsDatabase db, CiconiaConfiguration configuration) {
		this.mappingsContainer = mappingsContainer;
		this.objectsDatabase = db;
		this.configuration = configuration;
		handlers.addAll(defaultHandlers);
		handlers.addAll(configuration.getParameterAnnotationHandlers());
	}

	static void initialize(MappingsContainer mappingsContainer, ObjectsDatabase objectsDatabase, CiconiaConfiguration configuration) {
		instance = new CiconiaHandler(mappingsContainer, objectsDatabase, configuration);
	}

	/**
	 * Handles the request and returns the result.
	 *
	 * @param jsonObject the request JSON object
	 * @param requestMethod the request method
	 * @return method execution result or {@link #VOID} if method is void
	 * @see <a href="https://ciconia.maxsavteam.com/docs/#/ciconia-handler?id=json-format">Request JSON format</a>
	 * */
	public Object handle(JSONObject jsonObject, RequestMethod requestMethod){
		return handle(jsonObject, requestMethod, new ObjectsDatabase());
	}

	/**
	 * Handles the request and returns the result.
	 *
	 * @param jsonObject the request JSON object
	 * @param requestMethod the request method
	 * @param contextualDatabase database containing objects, which are related to current request
	 * @return method execution result or {@link #VOID} if method is void
	 * @see <a href="https://ciconia.maxsavteam.com/docs/#/ciconia-handler?id=json-format">Request JSON format</a>
	 * */
	public Object handle(JSONObject jsonObject, RequestMethod requestMethod, @Nonnull ObjectsDatabase contextualDatabase) {
		String methodName = jsonObject.getString("method");

		if(methodName.startsWith(String.valueOf(configuration.getPathSeparator())))
			methodName = methodName.substring(1);

		MappingsContainer.MethodSearchResult result = mappingsContainer.findMethod(methodName, requestMethod);
		if (result == null)
			throw new MethodNotFoundException(methodName + " (" + requestMethod + ")");
		Controller controller = result.getController();
		ExecutableMethod executableMethod = result.getMethod();
		Map<String, String> pathVariablesMap = result.getPathVariablesMap();

		JSONObject paramsJsonObject = jsonObject.optJSONObject("params", new JSONObject());

		MethodExecutionContext context = new MethodExecutionContext(executableMethod, controller);
		context.setParams(paramsJsonObject);
		context.setPathVariablesMap(pathVariablesMap);
		context.setContextualDatabase(contextualDatabase);

		try {
			return processMethod(context);
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new ExecutionException(e);
		}
	}

	private Object processMethod(MethodExecutionContext executionContext) throws InvocationTargetException, IllegalAccessException {
		ExecutableMethod method = executionContext.getExecutableMethod();
		Controller controller = executionContext.getController();
		JSONObject params = executionContext.getParams();
		Map<String, String> pathVariablesMap = executionContext.getPathVariablesMap();
		ObjectsDatabase contextualDatabase = executionContext.getContextualDatabase();

		RequestContext context = new RequestContext()
				.setObjectsDatabase(objectsDatabase.immutable())
				.setContextualObjectsDatabase(contextualDatabase.immutable())
				.setParameters(Collections.unmodifiableMap(params.toMap()))
				.setParametersJsonObject(params) // TODO: 29.08.2022 make json object unmodifiable
				.setPathVariables(Collections.unmodifiableMap(pathVariablesMap))
				.setMethodDeclarationPath(controller.getaClass().getName() + "#" + method.getMethod().getName())
				.setMethodMapping(controller.getMappingName() + configuration.getPathSeparator() + method.getMappingWrapper().getMappingName());

		List<ExecutableMethod.Argument> arguments = method.getArguments();
		Object[] methodArguments = new Object[arguments.size()];
		for (int i = 0; i < arguments.size(); i++) {
			ExecutableMethod.Argument argument = arguments.get(i);
			List<Annotation> annotations = argument.getAnnotations();
			Converter converter = new Converter(argument.getParameterGenericType(), argument.getParameterClass());
			boolean found = false;
			for(Annotation annotation : annotations){
				ParameterAnnotationHandler handler = getHandler(annotation);
				if(handler == null)
					continue;
				Optional<Object> op = handler.handle(annotation, argument.getParameterClass(), converter, context);
				if(op.isPresent()){
					found = true;
					Object obj = op.get();
					if(obj == ParameterAnnotationHandler.NULL_VALUE)
						methodArguments[i] = null;
					else
						methodArguments[i] = op.get();
					break;
				}
			}
			if(!found) {
				Class<?> argumentType = argument.getParameterClass();
				Optional<?> op = objectsDatabase.findObject(argumentType);
				if(op.isPresent())
					methodArguments[i] = op.get();
				else
					methodArguments[i] = contextualDatabase.findObject(argumentType).orElse(null);
			}
		}

		Object result = method.getMethod().invoke(controller.getClassInstance(), methodArguments);
		if(method.getMethod().getReturnType().equals(Void.TYPE))
			return VOID;
		return result;
	}

	private ParameterAnnotationHandler getHandler(Annotation annotation){
		for(ParameterAnnotationHandler handler : handlers){
			if(handler.supports(annotation.annotationType()))
				return handler;
		}
		return null;
	}

	private static class MethodExecutionContext {
		private final ExecutableMethod executableMethod;
		private final Controller controller;

		private JSONObject params;
		private Map<String, String> pathVariablesMap;
		private ObjectsDatabase contextualDatabase;

		public MethodExecutionContext(ExecutableMethod executableMethod, Controller controller) {
			this.executableMethod = executableMethod;
			this.controller = controller;
		}

		public ExecutableMethod getExecutableMethod() {
			return executableMethod;
		}

		public Controller getController() {
			return controller;
		}

		public JSONObject getParams() {
			return params;
		}

		public void setParams(JSONObject params) {
			this.params = params;
		}

		public Map<String, String> getPathVariablesMap() {
			return pathVariablesMap;
		}

		public void setPathVariablesMap(Map<String, String> pathVariablesMap) {
			this.pathVariablesMap = pathVariablesMap;
		}

		public ObjectsDatabase getContextualDatabase() {
			return contextualDatabase;
		}

		public void setContextualDatabase(ObjectsDatabase contextualDatabase) {
			this.contextualDatabase = contextualDatabase;
		}
	}

}
