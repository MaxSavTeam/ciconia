package com.maxsavteam.ciconia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applies to custom parameter annotation and indicates, that it should be handled by appreciate handler.
 * @see <a href="https://ciconia.maxsavteam.com/docs/#/configuration/parameter-annotation-handler">Guide</a>
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ParameterAnnotation {
}
