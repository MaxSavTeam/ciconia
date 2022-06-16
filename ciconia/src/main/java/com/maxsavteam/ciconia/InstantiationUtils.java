package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotations.Component;
import com.maxsavteam.ciconia.components.ComponentsDatabase;
import com.maxsavteam.ciconia.exceptions.InstantiationException;

import java.lang.reflect.Constructor;
import java.util.List;

class InstantiationUtils {

	private InstantiationUtils(){}

	public static ComponentsDatabase instantiateComponents(List<com.maxsavteam.ciconia.components.Component> components){
		ComponentsDatabase componentsDatabase = new ComponentsDatabase();
		for(com.maxsavteam.ciconia.components.Component component : components) {
			Constructor<?> ctor = component.findPreferredConstructor();
			instantiate(component, ctor, componentsDatabase);
			componentsDatabase.addComponent(component);
		}
		return componentsDatabase;
	}

	public static void instantiate(com.maxsavteam.ciconia.components.Component component, Constructor<?> ctor, ComponentsDatabase componentsDatabase){
		Object[] args = new Object[ctor.getParameterCount()];
		for(int i = 0; i < args.length; i++){
			Class<?> parameter = ctor.getParameterTypes()[i];
			if(parameter.isAnnotationPresent(Component.class)){
				int finalI = i;
				componentsDatabase.findComponent(parameter).ifPresent(c -> args[finalI] = c.getClassInstance());
			}
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
