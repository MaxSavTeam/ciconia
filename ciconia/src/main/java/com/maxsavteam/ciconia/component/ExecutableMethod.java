package com.maxsavteam.ciconia.component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Represents method with mapping in controller.
 * @author Max Savitsky
 * */
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

		private final Class<?> parameterClass;
		private final Type parameterGenericType;

		// list contains only annotations that are annotated with ParameterAnnotation
		private final List<Annotation> annotations;

		public Argument(Class<?> parameterClass, Type parameterGenericType, List<Annotation> annotations) {
			this.parameterClass = parameterClass;
			this.parameterGenericType = parameterGenericType;
			this.annotations = annotations;
		}

		public Class<?> getParameterClass() {
			return parameterClass;
		}

		public List<Annotation> getAnnotations() {
			return annotations;
		}

		public Type getParameterGenericType() {
			return parameterGenericType;
		}
	}

}
