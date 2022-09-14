package com.maxsavteam.ciconia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates, that method should be called after all components are initialized.
 * @see <a href="https://ciconia.maxsavteam.com/docs/#/configuration/configuration-annotation?id=post-initialization">Documentation</a>
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostInitialization {

	int order() default 0;

}
