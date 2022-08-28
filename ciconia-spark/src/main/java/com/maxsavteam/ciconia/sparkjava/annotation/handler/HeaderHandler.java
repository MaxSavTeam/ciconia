package com.maxsavteam.ciconia.sparkjava.annotation.handler;

import com.maxsavteam.ciconia.annotation.handler.RequestContext;
import com.maxsavteam.ciconia.annotation.handler.ParameterAnnotationHandler;
import com.maxsavteam.ciconia.converter.Converter;
import com.maxsavteam.ciconia.sparkjava.annotation.Header;
import spark.Request;

import java.lang.annotation.Annotation;
import java.util.Optional;

public class HeaderHandler implements ParameterAnnotationHandler {

	@Override
	public boolean supports(Class<? extends Annotation> annotationClass) {
		return Header.class.equals(annotationClass);
	}

	@Override
	public Optional<Object> handle(Annotation annotation, Class<?> parameterType, Converter converter, RequestContext context) {
		Header header = (Header) annotation;
		String headerName = header.value();
		Request request = Utils.getRequest(context.getContextualObjectsDatabase());
		String headerValue = request.headers(headerName);
		if(headerValue == null)
			return Optional.of(NULL_VALUE);
		return Optional.of(headerValue);
	}
}
