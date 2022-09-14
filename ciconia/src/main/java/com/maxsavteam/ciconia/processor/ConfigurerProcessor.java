package com.maxsavteam.ciconia.processor;

import com.maxsavteam.ciconia.annotation.ObjectFactory;
import com.maxsavteam.ciconia.annotation.PostInitialization;
import com.maxsavteam.ciconia.component.Configurer;
import com.maxsavteam.ciconia.component.ObjectFactoryMethod;
import com.maxsavteam.ciconia.component.PostInitializationMethod;
import com.maxsavteam.ciconia.exception.IllegalObjectFactoryMethodDeclarationException;
import com.maxsavteam.ciconia.exception.IllegalPostInitializationMethodDeclaration;
import com.maxsavteam.ciconia.exception.InvalidConfigurationDeclarationException;
import com.maxsavteam.ciconia.exception.MethodNotAccessibleException;
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
		int modifiers = method.getModifiers();
		if (Modifier.isStatic(modifiers))
			throw new IllegalObjectFactoryMethodDeclarationException("Object factory method cannot be static");
		if(!Modifier.isPublic(modifiers))
			throw new IllegalObjectFactoryMethodDeclarationException("Object factory method must be public");

		Class<?> returnType = method.getReturnType();
		if (returnType == Void.TYPE || returnType.isPrimitive())
			throw new IllegalObjectFactoryMethodDeclarationException("Method should not return void or primitive type");
		if (!method.isAccessible())
			throw new MethodNotAccessibleException("Method is not accessible: " + CiconiaUtils.getMethodDeclarationString(method));

		return new ObjectFactoryMethod(method);
	}

	private PostInitializationMethod processPostInitializationMethod(Method method){
		int modifiers = method.getModifiers();
		if (Modifier.isStatic(modifiers))
			throw new IllegalPostInitializationMethodDeclaration("Post initialization method cannot be static");
		if(!Modifier.isPublic(modifiers))
			throw new IllegalPostInitializationMethodDeclaration("Post initialization method must be public");

		if (!method.isAccessible())
			throw new MethodNotAccessibleException("Method is not accessible: " + CiconiaUtils.getMethodDeclarationString(method));

		PostInitialization annotation = method.getAnnotation(PostInitialization.class);

		return new PostInitializationMethod(method, annotation.order());
	}

}
