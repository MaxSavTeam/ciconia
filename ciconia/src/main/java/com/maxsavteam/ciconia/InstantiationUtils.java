package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotation.Component;
import com.maxsavteam.ciconia.component.ComponentsDatabase;
import com.maxsavteam.ciconia.component.ObjectsDatabase;
import com.maxsavteam.ciconia.exception.InstantiationException;

import java.lang.reflect.Constructor;
import java.util.List;

class InstantiationUtils {

	private InstantiationUtils(){}

	public static void instantiateComponents(List<com.maxsavteam.ciconia.component.Component> components, ObjectsDatabase objectsDatabase){
		for(com.maxsavteam.ciconia.component.Component component : components) {
			Constructor<?> ctor = component.findPreferredConstructor();
			instantiate(component, ctor, objectsDatabase);
			objectsDatabase.addObject(component.getClassInstance());
		}
	}

	public static void instantiate(com.maxsavteam.ciconia.component.Component component, Constructor<?> ctor, ObjectsDatabase objectsDatabase){
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
		component.setClassInstance(instance);
	}

}
