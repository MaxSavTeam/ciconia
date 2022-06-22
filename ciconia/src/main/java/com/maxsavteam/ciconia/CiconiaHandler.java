package com.maxsavteam.ciconia;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxsavteam.ciconia.annotation.Param;
import com.maxsavteam.ciconia.annotation.RequestMethod;
import com.maxsavteam.ciconia.annotation.ValueConstants;
import com.maxsavteam.ciconia.component.Controller;
import com.maxsavteam.ciconia.component.ExecutableMethod;
import com.maxsavteam.ciconia.component.ObjectsDatabase;
import com.maxsavteam.ciconia.exception.ExecutionException;
import com.maxsavteam.ciconia.exception.IncompatibleClassException;
import com.maxsavteam.ciconia.exception.MethodNotFoundException;
import com.maxsavteam.ciconia.exception.ParameterNotPresentException;
import com.maxsavteam.ciconia.tree.Tree;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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

	private final Tree tree;
	private final ObjectsDatabase objectsDatabase;
	private final CiconiaConfiguration configuration;

	private CiconiaHandler(Tree tree, ObjectsDatabase db, CiconiaConfiguration configuration) {
		this.tree = tree;
		this.objectsDatabase = db;
		this.configuration = configuration;
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

	private Object processMethod(MethodExecutionContext context) throws InvocationTargetException, IllegalAccessException {
		ExecutableMethod method = context.getExecutableMethod();
		Controller controller = context.getController();
		JSONObject params = context.getParams();
		Map<String, String> pathVariablesMap = context.getPathVariablesMap();
		ObjectsDatabase contextualDatabase = context.getContextualDatabase();

		List<ExecutableMethod.Argument> arguments = method.getArguments();
		Object[] methodArguments = new Object[arguments.size()];
		for (int i = 0; i < arguments.size(); i++) {
			ExecutableMethod.Argument argument = arguments.get(i);
			if (argument.isParameterized()) {
				Param param = argument.getParam();
				Object paramObject = params == null ? null : params.opt(param.value());
				if (paramObject == null) {
					if (param.required()) {
						throw new ParameterNotPresentException(
								String.format(
										"Parameter \"%s\" is not present, but required for method \"%s\" (%s)",
										param.value(),
										controller.getMappingName() + configuration.getPathSeparator() + method.getMappingWrapper().getMappingName(),
										controller.getComponentClass().getName() + "#" + method.getMethod().getName()
								)
						);
					} else {
						paramObject = ValueConstants.DEFAULT_NONE.equals(param.defaultValue()) ? null : param.defaultValue();
					}
				}
				methodArguments[i] = convertToParameterType(argument.getArgumentType(), paramObject, param.value());
			}else if(argument.isPathVariable()){
				String variableName = argument.getPathVariable().value();
				String variableValue = pathVariablesMap.get(variableName);
				methodArguments[i] = convertToParameterType(argument.getArgumentType(), variableValue, variableName);
			} else {
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

	private Object convertToParameterType(Class<?> cl, Object param, String paramName){
		if(param == null)
			return null;
		if(cl.isAssignableFrom(param.getClass()))
			return param;
		if(param instanceof JSONObject){
			try {
				return new ObjectMapper().readValue(param.toString(), cl);
			} catch (JsonProcessingException e) {
				throw new IncompatibleClassException(e);
			}
		}
		if(cl.isAssignableFrom(List.class) && param instanceof JSONArray){
			List<Object> list = new ArrayList<>();
			JSONArray jsonArray = (JSONArray) param;
			for(int i = 0; i < jsonArray.length(); i++){
				list.add(jsonArray.get(i));
			}
			return list;
		}

		if(param instanceof String){
			Object result = StringConverter.tryToConvert((String) param, cl);
			if(result != null)
				return result;
		}

		throw new IncompatibleClassException(
				String.format(
						"Parameter \"%s\" cannot be converted to declared type %s",
						paramName,
						cl.getName()
				)
		);
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
