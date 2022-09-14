package com.maxsavteam.ciconia.processor;

import com.maxsavteam.ciconia.CiconiaConfiguration;
import com.maxsavteam.ciconia.annotation.Mapping;
import com.maxsavteam.ciconia.annotation.ParameterAnnotation;
import com.maxsavteam.ciconia.component.Controller;
import com.maxsavteam.ciconia.component.ExecutableMethod;
import com.maxsavteam.ciconia.component.MappingWrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ControllersProcessor {

	private final CiconiaConfiguration configuration;

	public ControllersProcessor(CiconiaConfiguration configuration) {
		this.configuration = configuration;
	}

	public Controller processControllerClass(Class<?> cl){
		Mapping annotation = cl.getAnnotation(Mapping.class);
		requireValidMapping(annotation.value(), cl.getName(), configuration.getPathSeparator());
		ArrayList<ExecutableMethod> methods = new ArrayList<>();
		for(Method method : cl.getDeclaredMethods()){
			Optional<ExecutableMethod> op = processMethod(method);
			op.ifPresent(methods::add);
		}
		String mapping = annotation.value();
		String pathSeparator = String.valueOf(configuration.getPathSeparator());
		if(mapping.startsWith(pathSeparator))
			mapping = mapping.substring(1);
		if(mapping.endsWith(pathSeparator))
			mapping = mapping.substring(0, mapping.length() - 1);
		return new Controller(cl, mapping, methods);
	}

	private Optional<ExecutableMethod> processMethod(Method method){
		Mapping mapping = method.getAnnotation(Mapping.class);
		if(mapping == null)
			return Optional.empty();

		int modifiers = method.getModifiers();
		if(Modifier.isStatic(modifiers)
				|| !Modifier.isPublic(modifiers))
			return Optional.empty();

		requireValidMapping(mapping.value(), method.getDeclaringClass().getName() + "#" + method.getName(), configuration.getPathSeparator());

		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Class<?>[] parameterTypes = method.getParameterTypes();
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		ArrayList<ExecutableMethod.Argument> arguments = new ArrayList<>();
		for(int i = 0; i < parameterTypes.length; i++){
			Annotation[] annotations = parameterAnnotations[i];

			List<Annotation> parameterAnnotationsList = new ArrayList<>();
			for(Annotation annotation : annotations){
				if(annotation.annotationType().isAnnotationPresent(ParameterAnnotation.class))
					parameterAnnotationsList.add(annotation);
			}

			Class<?> parameterType = parameterTypes[i];
			Type parameterGenericType = genericParameterTypes[i];
			ExecutableMethod.Argument argument = new ExecutableMethod.Argument(parameterType, parameterGenericType, parameterAnnotationsList);
			arguments.add(argument);
		}

		String mappingName = mapping.value();
		if(mappingName.startsWith(String.valueOf(configuration.getPathSeparator())))
			mappingName = mappingName.substring(1);

		MappingWrapper mappingWrapper = new MappingWrapper(mappingName, Arrays.asList(mapping.method()), configuration);
		return Optional.of(new ExecutableMethod(method, mappingWrapper, arguments));
	}

	private static void requireValidMapping(String value, String entityName, char pathSeparator){
		String separator = String.valueOf(pathSeparator);
		String suf = "(" + entityName + ")";
		if(value.contains(" "))
			throw new IllegalArgumentException("Mapping should not contain whitespaces. " + suf);
		if(value.contains(separator + separator))
			throw new IllegalArgumentException("Mapping should not contain 2 (or more) separators in row. " + suf);
	}

}
