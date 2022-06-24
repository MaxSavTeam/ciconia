package com.maxsavteam.ciconia.component;

import com.maxsavteam.ciconia.annotation.Param;
import com.maxsavteam.ciconia.annotation.PathVariable;

import java.lang.annotation.Annotation;
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

		// list contains only annotations that are annotated with ParameterAnnotation
		private final List<Annotation> annotations;

		public Argument(Class<?> argumentType, List<Annotation> annotations) {
			this.argumentType = argumentType;
			this.annotations = annotations;
		}

		public Class<?> getArgumentType() {
			return argumentType;
		}

		public List<Annotation> getAnnotations() {
			return annotations;
		}
	}

}
