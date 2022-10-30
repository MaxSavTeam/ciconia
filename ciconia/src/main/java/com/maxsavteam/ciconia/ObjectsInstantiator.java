package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.component.Component;
import com.maxsavteam.ciconia.component.InstantiatableObject;
import com.maxsavteam.ciconia.component.ObjectsDatabase;
import com.maxsavteam.ciconia.exception.InstantiationException;

import java.lang.reflect.Constructor;

public class ObjectsInstantiator {

	private final Class<?> primarySource;
	private final ObjectsDatabase objectsDatabase;
	private final PropertyFieldsInjector propertyFieldsInjector;

	public ObjectsInstantiator(Class<?> primarySource, ObjectsDatabase objectsDatabase) {
		this.primarySource = primarySource;
		this.objectsDatabase = objectsDatabase;
		this.propertyFieldsInjector = new PropertyFieldsInjector(primarySource);
	}

	public void instantiate(InstantiatableObject instantiatableObject) {
		Object instance = instantiatableObject.create(objectsDatabase.immutable());
		objectsDatabase.addObject(instance, instantiatableObject.getaClass());
		instantiatableObject.setClassInstance(instance);

		if(instantiatableObject instanceof Component){
			propertyFieldsInjector.performInjection((Component) instantiatableObject);
		}
	}

	public static Object instantiateAsComponent(InstantiatableObject object, ObjectsDatabase objectsDatabase){
		Constructor<?> ctor = object.findPreferredConstructor();

		Object[] args = new Object[ctor.getParameterCount()];
		for(int i = 0; i < args.length; i++){
			Class<?> parameter = ctor.getParameterTypes()[i];
			int finalI = i;
			objectsDatabase.findObject(parameter)
					.ifPresent(obj -> args[finalI] = obj);
		}
		Object instance;
		try {
			instance = ctor.newInstance(args);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InstantiationException(e);
		}
		object.setClassInstance(instance);
		return instance;
	}

}
