package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.component.InstantiatableObject;
import com.maxsavteam.ciconia.component.ObjectsDatabase;
import com.maxsavteam.ciconia.exception.InstantiationException;

import java.lang.reflect.Constructor;
import java.util.List;

public class InstantiationUtils {

	private InstantiationUtils(){}

	public static void instantiateComponents(List<InstantiatableObject> objects, ObjectsDatabase objectsDatabase){
		ObjectsDatabase immutableDatabase = objectsDatabase.immutable();
		for(InstantiatableObject object : objects) {
			Object instance = object.create(immutableDatabase);
			objectsDatabase.addObject(instance);
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
