package com.maxsavteam.ciconia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates, that path variable should be passed to parameter.
 *
 * <pre>{@code
 * @Mapping("/process/{id}")
 * public void processUser(@PathVariable("id") String id){
 *    // ...
 * }
 * }</pre>
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@ParameterAnnotation
public @interface PathVariable {

	String value();

}
