package com.maxsavteam.ciconia.component;

import com.maxsavteam.ciconia.annotation.Param;
import com.maxsavteam.ciconia.annotation.PathVariable;

import java.lang.reflect.Method;
import java.util.List;

public class ExecutableMethod {

	private final Method method;
	private final List<Argument> arguments;
	private final MappingWrapper mappingWrapper;

	public ExecutableMethod(Method method, MappingWrapper mappingWrapper, List<Argument> arguments) {
		this.method = method;
		this.arguments = arguments;
		this.mappingWrapper = mappingWrapper;
	}

	public MappingWrapper getMappingWrapper() {
		return mappingWrapper;
	}

	public List<Argument> getArguments() {
		return arguments;
	}

	public Method getMethod() {
		return method;
	}

	public static class Argument {

		private final Class<?> argumentType;
		private final Param param;
		private final PathVariable pathVariable;

		public Argument(Class<?> argumentType, Param param, PathVariable pathVariable) {
			this.argumentType = argumentType;
			this.param = param;
			this.pathVariable = pathVariable;
		}

		public Class<?> getArgumentType() {
			return argumentType;
		}

		public boolean isParameterized() {
			return param != null;
		}

		public boolean isPathVariable() {
			return pathVariable != null;
		}

		public PathVariable getPathVariable() {
			return pathVariable;
		}

		public Param getParam() {
			return param;
		}
	}

}
