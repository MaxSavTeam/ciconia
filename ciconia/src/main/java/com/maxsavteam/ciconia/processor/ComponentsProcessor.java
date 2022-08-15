package com.maxsavteam.ciconia.processor;

import com.maxsavteam.ciconia.component.Component;
import com.maxsavteam.ciconia.exception.InvalidComponentDeclarationException;

import java.lang.reflect.Modifier;

public class ComponentsProcessor {

	public Component process(Class<?> cl){
		int modifiers = cl.getModifiers();

		if(Modifier.isAbstract(modifiers))
			throw new InvalidComponentDeclarationException("Component cannot be abstract class");

		if(Modifier.isInterface(modifiers))
			throw new InvalidComponentDeclarationException("Component cannot be interface");

		if(!Modifier.isPublic(modifiers) || cl.getDeclaringClass() != null)
			throw new InvalidComponentDeclarationException("Component should be public top-level class");
		return new Component(cl);
	}

}
