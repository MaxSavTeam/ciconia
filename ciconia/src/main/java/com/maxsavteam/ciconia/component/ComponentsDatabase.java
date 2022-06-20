package com.maxsavteam.ciconia.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ComponentsDatabase {

	private final Map<String, Component> componentMap = new HashMap<>();

	public void addComponent(Component component){
		componentMap.put(component.getComponentClass().getName(), component);
	}

	public Optional<Component> findComponent(Class<?> cl){
		Component component = componentMap.get(cl.getName());
		if(component == null)
			return Optional.empty();
		return Optional.of(component);
	}

}
