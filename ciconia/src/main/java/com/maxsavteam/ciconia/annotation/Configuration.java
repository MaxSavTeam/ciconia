package com.maxsavteam.ciconia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates, that class should be treated as {@link com.maxsavteam.ciconia.component.InstantiatableComponent}
 * @see <a href="https://ciconia.maxsavteam.com/docs/#/configuration/configuration-annotation">Documentation</a>
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Configuration {
}
