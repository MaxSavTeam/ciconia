package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotation.RequestMethod;
import com.maxsavteam.ciconia.annotation.handler.Context;
import com.maxsavteam.ciconia.annotation.handler.ParamHandler;
import com.maxsavteam.ciconia.annotation.handler.ParameterAnnotationHandler;
import com.maxsavteam.ciconia.annotation.handler.PathVariableHandler;
import com.maxsavteam.ciconia.component.Controller;
import com.maxsavteam.ciconia.component.ExecutableMethod;
import com.maxsavteam.ciconia.component.ObjectsDatabase;
import com.maxsavteam.ciconia.exception.ExecutionException;
import com.maxsavteam.ciconia.exception.MethodNotFoundException;
import com.maxsavteam.ciconia.tree.Tree;
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

	private final Tree tree;
	private final ObjectsDatabase objectsDatabase;
	private final CiconiaConfiguration configuration;

	private final List<ParameterAnnotationHandler> handlers = new ArrayList<>();

	private CiconiaHandler(Tree tree, ObjectsDatabase db, CiconiaConfiguration configuration) {
		this.tree = tree;
		this.objectsDatabase = db;
		this.configuration = configuration;
		handlers.addAll(defaultHandlers);
		handlers.addAll(configuration.getParameterAnnotationHandlers());
	}

	static void initialize(Tree tree, ObjectsDatabase objectsDatabase, CiconiaConfiguration configuration) {
		instance = new CiconiaHandler(tree, objectsDatabase, configuration);
	}

	public Object handle(JSONObject jsonObject, RequestMethod requestMethod){
		return handle(jsonObject, requestMethod, new ObjectsDatabase());
	}

	public Object handle(JSONObject jsonObject, RequestMethod requestMethod, @Nonnull ObjectsDatabase contextualDatabase) {
		String methodName = jsonObject.getString("method");

		if(methodName.startsWith(String.valueOf(configuration.getPathSeparator())))
			methodName = methodName.substring(1);

		Optional<Tree.MethodSearchResult> op = tree.findMethod(methodName, requestMethod);
		if (op.isEmpty())
			throw new MethodNotFoundException(methodName + " (" + requestMethod + ")");
		Tree.MethodSearchResult result = op.get();
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

		Context context = new Context()
				.setComponentsDatabase(ObjectsDatabase.immutable(objectsDatabase))
				.setContextualObjectsDatabase(ObjectsDatabase.immutable(contextualDatabase))
				.setParameters(Collections.unmodifiableMap(params.toMap()))
				.setPathVariables(Collections.unmodifiableMap(pathVariablesMap))
				.setMethodPath(controller.getComponentClass().getName() + "#" + method.getMethod().getName())
				.setMethodMapping(controller.getMappingName() + configuration.getPathSeparator() + method.getMappingWrapper().getMappingName());

		List<ExecutableMethod.Argument> arguments = method.getArguments();
		Object[] methodArguments = new Object[arguments.size()];
		for (int i = 0; i < arguments.size(); i++) {
			ExecutableMethod.Argument argument = arguments.get(i);
			List<Annotation> annotations = argument.getAnnotations();
			boolean found = false;
			for(Annotation annotation : annotations){
				ParameterAnnotationHandler handler = getHandler(annotation);
				if(handler == null)
					continue;
				Optional<Object> op = handler.handle(annotation, argument.getArgumentType(), context);
				if(op.isPresent()){
					found = true;
					methodArguments[i] = op.get();
					break;
				}
			}
			if(!found) {
				Optional<Object> op = objectsDatabase.findObject(argument.getArgumentType());
				methodArguments[i] = op
						.or(()->contextualDatabase.findSuitableObject(argument.getArgumentType()))
						.orElse(null);
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
