package com.maxsavteam.ciconia.annotation.handler;

import com.maxsavteam.ciconia.annotation.PathVariable;
import com.maxsavteam.ciconia.converter.Converter;
import com.maxsavteam.ciconia.exception.IncompatibleClassException;
import com.maxsavteam.ciconia.exception.InvalidPathVariableException;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * {@link PathVariable} processor
 * @author Max Savitsky
 * */
public class PathVariableHandler implements ParameterAnnotationHandler {

	@Override
	public boolean supports(Class<? extends Annotation> annotationClass) {
		return PathVariable.class.equals(annotationClass);
	}

	@Override
	public Optional<Object> handle(Annotation annotation, Converter converter, RequestContext context) {
		PathVariable pathVariable = (PathVariable) annotation;
		String variableName = pathVariable.value();
		if(!context.getPathVariables().containsKey(variableName)) {
			throw new InvalidPathVariableException(
					String.format(
							"Path variable \"%s\" is not declared in method path (%s)",
							variableName,
							context.getMethodDeclarationPath()
					)
			);
		}
		String variableValue = context.getPathVariables().get(variableName);
		Optional<Object> op = converter.convertToParameterType(variableValue);
		if(op.isPresent())
			return op;
		throw new IncompatibleClassException(
				String.format(
						"Path variable \"%s\" cannot be converted to declared type %s",
						pathVariable.value(),
						converter.getParameterClass().getName()
				)
		);
	}
}
