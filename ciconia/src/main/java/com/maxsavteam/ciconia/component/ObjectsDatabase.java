package com.maxsavteam.ciconia.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ObjectsDatabase {

	private final Map<String, Object> objectMap = new HashMap<>();

	public void addObject(Object object){
		objectMap.put(object.getClass().getName(), object);
	}

	public Optional<Object> findObject(Class<?> clazz){
		if(objectMap.containsKey(clazz.getName()))
			return Optional.of(objectMap.get(clazz.getName()));
		return Optional.empty();
	}

	public Optional<Object> findSuitableObject(Class<?> clazz){
		return objectMap.values()
				.stream()
				.filter(entry -> clazz.isAssignableFrom(entry.getClass()))
				.findFirst();
	}

}
