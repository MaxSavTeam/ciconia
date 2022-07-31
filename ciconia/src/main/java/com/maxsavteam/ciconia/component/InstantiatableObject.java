package com.maxsavteam.ciconia.component;

import com.maxsavteam.ciconia.InstantiatableObjectFactory;
import com.maxsavteam.ciconia.exception.InstantiationException;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

public abstract class InstantiatableObject implements Dependant, InstantiatableObjectFactory {

	protected final Class<?> aClass;
	private Object classInstance;

	public InstantiatableObject(Class<?> aClass) {
		this.aClass = aClass;
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
		return Collections.emptyList();
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
	public Object create(ObjectsDatabase database) throws InstantiationException {
		Object instance = getFactory().create(database);
		setClassInstance(instance);
		return instance;
	}

	public abstract InstantiatableObjectFactory getFactory();

}
