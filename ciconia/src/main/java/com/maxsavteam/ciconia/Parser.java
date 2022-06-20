package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotation.Component;
import com.maxsavteam.ciconia.annotation.Mapping;
import com.maxsavteam.ciconia.annotation.Param;
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

	public List<com.maxsavteam.ciconia.components.Component> parse(){
		Set<Class<?>> set = reflections.get(Scanners.SubTypes.of(Scanners.TypesAnnotated.with(Component.class)).asClass());
		ArrayList<com.maxsavteam.ciconia.components.Component> components = new ArrayList<>();
		for(Class<?> cl : set){
			components.add(processComponent(cl));
		}
		return components;
	}

	private com.maxsavteam.ciconia.components.Component processComponent(Class<?> cl){
		com.maxsavteam.ciconia.components.Component component;
		if(cl.isAnnotationPresent(Mapping.class)){
			component = processControllerMapping(cl);
		}else{
			component = new com.maxsavteam.ciconia.components.Component(cl);
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
		String mapping = annotation.value();
		if(mapping.endsWith(String.valueOf(configuration.getPathSeparator())))
			mapping = mapping.substring(0, mapping.length() - 1);
		return new Controller(cl, mapping, methods);
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
		if(value.startsWith(separator))
			throw new IllegalArgumentException("Mapping should not start or end with separator. " + entityName);
		if(value.contains(separator + separator))
			throw new IllegalArgumentException("Mapping should not contain 2 (or more) separators in row. " + suf);
	}

}
