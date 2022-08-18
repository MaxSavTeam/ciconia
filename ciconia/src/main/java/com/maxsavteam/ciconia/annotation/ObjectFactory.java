package com.maxsavteam.ciconia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates, that method produces object, which can be used for injection into other components and factories.
 * Object factory should be public non-static method, which cannot be void or return primitive.<br>
 * <a href="https://ciconia.maxsavteam.com/docs/#/configuration/configuration-annotation?id=objects-factories">Documentation</a>
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ObjectFactory {
}
