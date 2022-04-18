package com.maxsavteam.ciconia.components;

import com.maxsavteam.ciconia.annotations.Mapping;
import com.maxsavteam.ciconia.annotations.Param;

import java.lang.reflect.Method;
import java.util.List;

public class ExecutableMethod {

	private final Method method;
	private final List<Argument> arguments;
	private final Mapping mapping;
	public ExecutableMethod(Method method, Mapping mapping, List<Argument> arguments) {
		this.method = method;
		this.arguments = arguments;
		this.mapping = mapping;
	}

	public String getMappingName() {
		return mapping.value();
	}

	public Mapping getMapping() {
		return mapping;
	}

	public List<Argument> getArguments() {
		return arguments;
	}

	public Method getMethod() {
		return method;
	}

	public static class Argument {

		private final Class<?> argumentType;

		private final boolean isParameterized;
		private final Param param;

		public Argument(Class<?> argumentType, Param param) {
			this.argumentType = argumentType;
			this.param = param;
			this.isParameterized = param != null;
		}

		public Class<?> getArgumentType() {
			return argumentType;
		}

		public boolean isParameterized() {
			return isParameterized;
		}

		public Param getParam() {
			return param;
		}
	}

}
