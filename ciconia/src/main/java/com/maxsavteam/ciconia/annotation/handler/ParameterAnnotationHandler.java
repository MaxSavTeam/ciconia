package com.maxsavteam.ciconia.annotation.handler;

import com.maxsavteam.ciconia.converter.Converter;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * Handles custom parameter annotations, which annotated with {@link com.maxsavteam.ciconia.annotation.ParameterAnnotation}.
 * @see <a href="https://ciconia.maxsavteam.com/docs/#/configuration/parameter-annotation-handler">Guide</a>
 * @author Max Savitsky
 * */
public interface ParameterAnnotationHandler {

	Object NULL_VALUE = Converter.NULL_VALUE;

	/**
	 * @param annotationClass annotation class
	 * @return true if handler can process annotation.
	 * */
	boolean supports(Class<? extends Annotation> annotationClass);

	/**
	 * Returns parameter value in Optional.<br>
	 * If Optional is empty, then next suitable handler will be used.<br>
	 * To return {@code null} use {@code Optional.of(NULL_VALUE)}.
	 *
	 * @param annotation Annotation that parameter is annotated with.
	 * @param parameterType Type of parameter.
	 * @param context Context of invocation.
	 * @return parameter value in Optional.
	 * */
	Optional<Object> handle(Annotation annotation, Class<?> parameterType, RequestContext context);

}
