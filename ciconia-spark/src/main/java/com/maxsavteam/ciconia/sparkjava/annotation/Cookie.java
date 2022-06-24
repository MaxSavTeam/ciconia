package com.maxsavteam.ciconia.sparkjava.annotation;

import com.maxsavteam.ciconia.annotation.ParameterAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@ParameterAnnotation
public @interface Cookie {

	String value();

}
