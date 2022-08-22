package com.maxsavteam.ciconia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates, that path variable should be passed to parameter.
 * <pre>
 * <code>
 * {@literal @}Mapping("/process/{id}")
 * public void processUser(@PathVariable("id") String id){
 *    // ...
 * }
 * </code>
 * </pre>
 * @see <a href="https://ciconia.maxsavteam.com/docs/#/components/controllers?id=path-variables">Path variables documentation</a>
 * @author Max Savitsky
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@ParameterAnnotation
public @interface PathVariable {

	String value();

}
