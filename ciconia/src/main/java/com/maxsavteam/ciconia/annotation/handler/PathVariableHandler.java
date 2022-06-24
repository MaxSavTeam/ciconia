package com.maxsavteam.ciconia.annotation.handler;

import com.maxsavteam.ciconia.annotation.PathVariable;
import com.maxsavteam.ciconia.converter.Converter;
import com.maxsavteam.ciconia.exception.IncompatibleClassException;

import java.lang.annotation.Annotation;
import java.util.Optional;

public class PathVariableHandler implements ParameterAnnotationHandler {

	@Override
	public boolean supports(Class<? extends Annotation> annotationClass) {
		return PathVariable.class.equals(annotationClass);
	}

	@Override
	public Optional<Object> handle(Annotation annotation, Class<?> parameterType, Context context) {
		PathVariable pathVariable = (PathVariable) annotation;
		String variableName = pathVariable.value();
		String variableValue = context.getPathVariables().get(variableName);
		Optional<Object> op = Converter.convertToParameterType(variableValue, parameterType);
		if(op.isPresent())
			return op;
		throw new IncompatibleClassException(
				String.format(
						"Path variable \"%s\" cannot be converted to declared type %s",
						pathVariable.value(),
						parameterType.getName()
				)
		);
	}
}
