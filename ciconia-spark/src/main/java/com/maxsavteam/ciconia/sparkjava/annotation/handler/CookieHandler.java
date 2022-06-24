package com.maxsavteam.ciconia.sparkjava.annotation.handler;

import com.maxsavteam.ciconia.annotation.handler.Context;
import com.maxsavteam.ciconia.annotation.handler.ParameterAnnotationHandler;
import com.maxsavteam.ciconia.sparkjava.annotation.Cookie;
import spark.Request;

import java.lang.annotation.Annotation;
import java.util.Optional;

public class CookieHandler extends BaseRequestHandler implements ParameterAnnotationHandler {

	@Override
	public boolean supports(Class<? extends Annotation> annotationClass) {
		return Cookie.class.equals(annotationClass);
	}

	@Override
	public Optional<Object> handle(Annotation annotation, Class<?> parameterType, Context context) {
		Cookie cookie = (Cookie) annotation;
		Request request = getRequest(context.getContextualObjectsDatabase());
		String cookieName = cookie.value();
		String cookieValue = request.cookie(cookieName);
		if(cookieValue == null)
			return Optional.of(NULL_VALUE);
		return Optional.of(cookieValue);
	}
}
