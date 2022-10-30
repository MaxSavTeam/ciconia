package com.maxsavteam.ciconia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicates, that field should be injected with value from properties file.
 * */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {

	String value();

	String filename() default "application.properties";

	Source source() default Source.FROM_RESOURCES;

	enum Source {
		FROM_RESOURCES, FROM_EXTERNAL_STORAGE
	}

}
