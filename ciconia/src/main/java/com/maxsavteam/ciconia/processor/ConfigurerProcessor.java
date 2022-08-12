package com.maxsavteam.ciconia.processor;

import com.maxsavteam.ciconia.annotation.ObjectFactory;
import com.maxsavteam.ciconia.component.Configurer;
import com.maxsavteam.ciconia.component.ObjectFactoryMethod;
import com.maxsavteam.ciconia.exception.IllegalObjectFactoryMethodDeclarationException;
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
		List<ObjectFactoryMethod> methods = new ArrayList<>();
		for(Method method : cl.getDeclaredMethods()){
			if(!method.isAnnotationPresent(ObjectFactory.class))
				continue;

			int modifiers = method.getModifiers();
			if(Modifier.isStatic(modifiers)
				|| !Modifier.isPublic(modifiers))
				continue;

			Class<?> returnType = method.getReturnType();
			if(returnType == Void.TYPE || returnType.isPrimitive())
				throw new IllegalObjectFactoryMethodDeclarationException("Method should not return void or primitive type");
			if(!method.trySetAccessible())
				throw new MethodNotAccessibleException("Method is not accessible: " + CiconiaUtils.getMethodDeclarationString(method));
			methods.add(new ObjectFactoryMethod(method));
		}

		return new Configurer(cl, methods);
	}

}
