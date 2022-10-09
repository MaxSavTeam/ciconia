package com.maxsavteam.ciconia.processor;

import com.maxsavteam.ciconia.annotation.ObjectFactory;
import com.maxsavteam.ciconia.annotation.PostInitialization;
import com.maxsavteam.ciconia.component.Configurer;
import com.maxsavteam.ciconia.component.ObjectFactoryMethod;
import com.maxsavteam.ciconia.component.PostInitializationMethod;
import com.maxsavteam.ciconia.exception.InvalidObjectFactoryMethodDeclarationException;
import com.maxsavteam.ciconia.exception.InvalidPostInitializationMethodDeclaration;
import com.maxsavteam.ciconia.exception.InvalidConfigurationDeclarationException;
import com.maxsavteam.ciconia.utils.CiconiaUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ConfigurerProcessor {

	public List<Configurer> process(List<Class<?>> classes) {
		List<Configurer> configurers = new ArrayList<>();
		for (Class<?> clazz : classes) {
			configurers.add(process(clazz));
		}
		return configurers;
	}

	public Configurer process(Class<?> cl) {
		int classModifiers = cl.getModifiers();
		if (Modifier.isInterface(classModifiers))
			throw new InvalidConfigurationDeclarationException("Configuration cannot be interface");
		if (Modifier.isAbstract(classModifiers))
			throw new InvalidConfigurationDeclarationException("Configuration cannot be abstract class");

		if (!Modifier.isPublic(classModifiers) || cl.getDeclaringClass() != null)
			throw new InvalidConfigurationDeclarationException("Configuration should be public top-level class");

		List<ObjectFactoryMethod> factoryMethods = new ArrayList<>();
		List<PostInitializationMethod> postInitializationMethods = new ArrayList<>();

		for (Method method : cl.getDeclaredMethods()) {
			if (method.isAnnotationPresent(ObjectFactory.class))
				factoryMethods.add(processObjectFactoryMethod(method));
			else if(method.isAnnotationPresent(PostInitialization.class))
				postInitializationMethods.add(processPostInitializationMethod(method));
		}

		return new Configurer(cl, factoryMethods, postInitializationMethods);
	}

	private ObjectFactoryMethod processObjectFactoryMethod(Method method){
		CiconiaUtils.checkMethodDeclaration(method, InvalidObjectFactoryMethodDeclarationException.class);

		Class<?> returnType = method.getReturnType();
		if (returnType == Void.TYPE || returnType.isPrimitive())
			throw new InvalidObjectFactoryMethodDeclarationException("Method should not return void or primitive type");

		return new ObjectFactoryMethod(method);
	}

	private PostInitializationMethod processPostInitializationMethod(Method method){
		CiconiaUtils.checkMethodDeclaration(method, InvalidPostInitializationMethodDeclaration.class);

		PostInitialization annotation = method.getAnnotation(PostInitialization.class);

		return new PostInitializationMethod(method, annotation.order());
	}

}
