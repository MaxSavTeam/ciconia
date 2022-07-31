package com.maxsavteam.ciconia.component;

import java.util.List;

public class Configurer extends InstantiatableComponent {

	private final List<ObjectFactoryMethod> methods;

	public Configurer(Class<?> aClass, List<ObjectFactoryMethod> methods) {
		super(aClass);
		this.methods = methods;
	}

	public List<ObjectFactoryMethod> getMethods() {
		return methods;
	}

}
