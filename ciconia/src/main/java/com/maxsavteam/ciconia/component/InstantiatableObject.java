package com.maxsavteam.ciconia.component;

import com.maxsavteam.ciconia.ObjectFactory;
import com.maxsavteam.ciconia.exception.InstantiationException;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public abstract class InstantiatableObject implements Dependant, ObjectFactory {

	protected final Class<?> aClass;
	protected final List<Class<?>> dependenciesClasses = new ArrayList<>();
	private Object classInstance;

	public InstantiatableObject(Class<?> aClass) {
		this.aClass = aClass;

		analyzeDependencies();
	}

	protected void analyzeDependencies(){

	}

	public Constructor<?> findPreferredConstructor(){
		Constructor<?>[] constructors = aClass.getConstructors();
		if(constructors.length != 1)
			throw new InstantiationException("There is no default public constructor for "
					+ aClass.getName());
		return constructors[0];
	}

	@Override
	public List<Class<?>> getDependenciesClasses() {
		return dependenciesClasses;
	}

	public Class<?> getaClass() {
		return aClass;
	}

	public Object getClassInstance() {
		return classInstance;
	}

	public void setClassInstance(Object classInstance) {
		this.classInstance = classInstance;
	}

	@Override
	public Object create(ObjectsDatabase database) {
		Object instance = getFactory().create(database);
		setClassInstance(instance);
		return instance;
	}

	public abstract ObjectFactory getFactory();

}
