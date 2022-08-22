package com.maxsavteam.ciconia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates, that parameter from request should be passed to parameter method.<br>
 * Framework will try to convert parameter to method parameter type, otherwise {@link com.maxsavteam.ciconia.exception.IncompatibleClassException} will be thrown.<br>
 * If method parameter (M) is String and parameter from request (R) is number, boolean or string, then it will be passed as it is.<br>
 * If M is assignable from R, then R will be passed as it is.<br>
 * If M is List and R is array, then every element of R will be converted to M element type and added to List.
 * <p>
 * Parameter can be non-required. If parameter is not present, then default value will be used (by default, default value is {@code null}).<br>
 * If required parameter is not present, then {@link com.maxsavteam.ciconia.exception.ParameterNotPresentException} will be thrown.<br>
 * */
@ParameterAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

	String value();

	boolean required() default true;

	String defaultValue() default ValueConstants.DEFAULT_NONE;

}
