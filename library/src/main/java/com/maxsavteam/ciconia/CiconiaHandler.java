package com.maxsavteam.ciconia;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxsavteam.ciconia.annotations.Param;
import com.maxsavteam.ciconia.annotations.RequestMethod;
import com.maxsavteam.ciconia.components.Component;
import com.maxsavteam.ciconia.components.ComponentsDatabase;
import com.maxsavteam.ciconia.components.Controller;
import com.maxsavteam.ciconia.components.ExecutableMethod;
import com.maxsavteam.ciconia.exceptions.ExecutionException;
import com.maxsavteam.ciconia.exceptions.IncompatibleClassException;
import com.maxsavteam.ciconia.exceptions.MethodNotFoundException;
import com.maxsavteam.ciconia.exceptions.ParameterNotPresentException;
import com.maxsavteam.ciconia.tree.Tree;
import com.maxsavteam.ciconia.utils.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CiconiaHandler {

	public static final Object VOID = new Object();
	public static final Object ASYNC_METHOD = new Object();

	private static CiconiaHandler instance;

	public static CiconiaHandler getInstance() {
		return instance;
	}

	private final Tree tree;
	private final ComponentsDatabase componentsDb;

	private CiconiaHandler(Tree tree, ComponentsDatabase db) {
		this.tree = tree;
		this.componentsDb = db;
	}

	static void initialize(Tree tree, ComponentsDatabase componentsDatabase) {
		instance = new CiconiaHandler(tree, componentsDatabase);
	}

	public Object handle(JSONObject jsonObject, RequestMethod requestMethod) {
		String methodName = jsonObject.getString("method");

		Optional<Pair<Controller, ExecutableMethod>> op = tree.findMethod(methodName, requestMethod);
		if (op.isEmpty())
			throw new MethodNotFoundException(methodName + " (" + requestMethod + ")");
		Controller controller = op.get().getFirst();
		ExecutableMethod executableMethod = op.get().getSecond();

		JSONObject paramsJsonObject = jsonObject.optJSONObject("params");
		try {
			return processMethod(executableMethod, paramsJsonObject, controller);
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new ExecutionException(e);
		}
	}

	private Object processMethod(ExecutableMethod method, JSONObject params, Controller controller) throws InvocationTargetException, IllegalAccessException {
		List<ExecutableMethod.Argument> arguments = method.getArguments();
		Object[] methodArguments = new Object[arguments.size()];
		for (int i = 0; i < arguments.size(); i++) {
			ExecutableMethod.Argument argument = arguments.get(i);
			if (argument.isParameterized()) {
				Param param = argument.getParam();
				Object paramObject = params == null ? null : params.opt(param.value());
				if (paramObject == null) {
					throw new ParameterNotPresentException(
							String.format(
									"Parameter \"%s\" is not present, but required for method \"%s\" (%s)",
									param.value(),
									controller.getMappingName() + "." + method.getMappingName(),
									controller.getComponentClass().getName() + "#" + method.getMethod().getName()
							)
					);
				}
				methodArguments[i] = convertToParameterType(argument.getArgumentType(), paramObject, param.value());
			} else {
				Component component = findSuitableComponent(argument.getArgumentType());
				if(component != null){
					methodArguments[i] = component.getClassInstance();
				}
			}
		}

		Object result = method.getMethod().invoke(controller.getClassInstance(), methodArguments);
		if(method.getMethod().getReturnType().equals(Void.TYPE))
			return VOID;
		return result;
	}

	private Component findSuitableComponent(Class<?> cl){
		Optional<Component> op = componentsDb.findComponent(cl);
		return op.orElse(null);
	}

	private Object convertToParameterType(Class<?> cl, Object param, String paramName){
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

		throw new IncompatibleClassException(
				String.format(
						"Parameter \"%s\" cannot be converted to declared type %s",
						paramName,
						cl.getName()
				)
		);
	}

}
