package com.maxsavteam.ciconia.component;

import com.maxsavteam.ciconia.exceptions.InstantiationException;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class Component {

	private final Class<?> componentClass;

	private final ArrayList<Class<?>> dependenciesClasses = new ArrayList<>();

	private Object classInstance;

	public Component(Class<?> componentClass) {
		this.componentClass = componentClass;

		analyzeDependencies();
	}

	private void analyzeDependencies(){
		Constructor<?> ctor = findPreferredConstructor();
		for(Class<?> cl : ctor.getParameterTypes()){
			if(cl.isAnnotationPresent(com.maxsavteam.ciconia.annotation.Component.class))
				dependenciesClasses.add(cl);
		}
	}

	public Constructor<?> findPreferredConstructor(){
		Constructor<?>[] constructors = componentClass.getConstructors();
		if(constructors.length != 1)
			throw new InstantiationException("There is no default public constructor for " + componentClass.getName());
		return constructors[0];
	}

	public List<Class<?>> getDependenciesClasses() {
		return dependenciesClasses;
	}

	public Class<?> getComponentClass() {
		return componentClass;
	}

	public Object getClassInstance() {
		return classInstance;
	}

	public void setClassInstance(Object classInstance) {
		this.classInstance = classInstance;
	}
}
