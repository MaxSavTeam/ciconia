package com.maxsavteam.ciconia.annotation.handler;

import com.maxsavteam.ciconia.converter.Converter;

import java.lang.annotation.Annotation;
import java.util.Optional;

public interface ParameterAnnotationHandler {

	Object NULL_VALUE = Converter.NULL_VALUE;

	boolean supports(Class<? extends Annotation> annotationClass);

	Optional<Object> handle(Annotation annotation, Class<?> parameterType, Context context);

}
