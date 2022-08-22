package com.maxsavteam.ciconia.component;

import java.util.List;

/**
 * Represents particular configuration class.
 * @see com.maxsavteam.ciconia.annotation.Configuration
 * @author Max Savitsky
 * */
public class Configurer extends InstantiatableComponent {

	private final List<ObjectFactoryMethod> methods;

	public Configurer(Class<?> aClass, List<ObjectFactoryMethod> methods) {
		super(aClass);
		this.methods = methods;
	}

	/**
	 * @return list of object factory methods
	 * @see com.maxsavteam.ciconia.annotation.ObjectFactory
	 * @see ObjectFactoryMethod
	 * */
	public List<ObjectFactoryMethod> getMethods() {
		return methods;
	}

}
