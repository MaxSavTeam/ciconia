package com.maxsavteam.ciconia.component;

import com.maxsavteam.ciconia.InstantiationUtils;
import com.maxsavteam.ciconia.ObjectFactory;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class Component extends InstantiatableObject {

	public Component(Class<?> aClass) {
		super(aClass);
	}

	@Override
	protected void analyzeDependencies() {
		Constructor<?> ctor = findPreferredConstructor();
		dependenciesClasses.addAll(Arrays.asList(ctor.getParameterTypes()));
	}

	@Override
	public ObjectFactory getFactory() {
		return database -> InstantiationUtils.instantiateAsComponent(this, database);
	}
}
