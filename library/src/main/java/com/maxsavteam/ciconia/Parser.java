package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotations.Mapping;
import com.maxsavteam.ciconia.annotations.Param;
import com.maxsavteam.ciconia.components.Component;
import com.maxsavteam.ciconia.components.Controller;
import com.maxsavteam.ciconia.components.ExecutableMethod;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

class Parser {

	private final Reflections reflections;
	private final CiconiaConfiguration configuration;

	public Parser(Class<?> primarySource, CiconiaConfiguration configuration) {
		this.reflections = new Reflections(primarySource.getPackageName());
		this.configuration = configuration;
	}

	public List<Component> parse(){
		Set<Class<?>> set = reflections.get(Scanners.SubTypes.of(Scanners.TypesAnnotated.with(com.maxsavteam.ciconia.annotations.Component.class)).asClass());
		ArrayList<Component> components = new ArrayList<>();
		for(Class<?> cl : set){
			components.add(processComponent(cl));
		}
		return components;
	}

	private Component processComponent(Class<?> cl){
		Component component;
		if(cl.isAnnotationPresent(Mapping.class)){
			component = processControllerMapping(cl);
		}else{
			component = new Component(cl);
		}
		return component;
	}

	private Controller processControllerMapping(Class<?> cl){
		Mapping annotation = cl.getAnnotation(Mapping.class);
		requireValidMapping(annotation.value(), cl.getName(), configuration.getPathSeparator());
		ArrayList<ExecutableMethod> methods = new ArrayList<>();
		for(Method method : cl.getDeclaredMethods()){
			Optional<ExecutableMethod> op = processMethod(method);
			op.ifPresent(methods::add);
		}
		return new Controller(cl, annotation.value(), methods);
	}

	private Optional<ExecutableMethod> processMethod(Method method){
		Mapping mapping = method.getAnnotation(Mapping.class);
		if(mapping == null)
			return Optional.empty();
		requireValidMapping(mapping.value(), method.getDeclaringClass().getName() + "#" + method.getName(), configuration.getPathSeparator());

		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Class<?>[] parameterTypes = method.getParameterTypes();
		ArrayList<ExecutableMethod.Argument> arguments = new ArrayList<>();
		for(int i = 0; i < parameterTypes.length; i++){
			Annotation[] annotations = parameterAnnotations[i];
			Param paramAnnotation = null;
			for(Annotation a : annotations){
				if(a instanceof Param){
					paramAnnotation = (Param) a;
					break;
				}
			}
			Class<?> parameterType = parameterTypes[i];
			ExecutableMethod.Argument argument = new ExecutableMethod.Argument(parameterType, paramAnnotation);
			arguments.add(argument);
		}
		return Optional.of(new ExecutableMethod(method, mapping, arguments));
	}

	private static void requireValidMapping(String value, String entityName, char pathSeparator){
		String separator = String.valueOf(pathSeparator);
		String suf = "(" + entityName + ")";
		if(value.contains(" "))
			throw new IllegalArgumentException("Mapping should not contain whitespaces. " + suf);
		if(value.startsWith(separator) || value.endsWith(separator))
			throw new IllegalArgumentException("Mapping should not start or end with separator. " + entityName);
		if(value.contains(separator + separator))
			throw new IllegalArgumentException("Mapping should not contain 2 (or more) separators in row. " + suf);
	}

}
