package com.maxsavteam.ciconia.component;

import com.maxsavteam.ciconia.InstantiatableObjectFactory;
import com.maxsavteam.ciconia.ObjectsInstantiator;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public class InstantiatableComponent extends InstantiatableObject {

	public InstantiatableComponent(Class<?> aClass) {
		super(aClass);
	}

	@Override
	public List<Class<?>> getDependenciesClasses() {
		Constructor<?> ctor = findPreferredConstructor();
		return Arrays.asList(ctor.getParameterTypes());
	}

	@Override
	public InstantiatableObjectFactory getFactory() {
		return database -> ObjectsInstantiator.instantiateAsComponent(this, database);
	}

}
