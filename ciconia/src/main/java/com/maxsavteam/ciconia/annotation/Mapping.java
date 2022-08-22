package com.maxsavteam.ciconia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies path, according to which method will be called. Mapping can contain path variables
 * @see <a href="https://ciconia.maxsavteam.com/docs/#/components/controllers?id=mapping">Mapping documentation</a>
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Mapping {
	String value() default "";

	RequestMethod[] method() default RequestMethod.GET;

}
