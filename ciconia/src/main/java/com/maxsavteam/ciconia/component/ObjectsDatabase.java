package com.maxsavteam.ciconia.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ObjectsDatabase {

	private final Map<String, Object> objectMap = new HashMap<>();

	public void addObject(Object component, Class<?> asClass){
		if(!asClass.isAssignableFrom(component.getClass()))
			throw new IllegalStateException("Class " + asClass.getName() + " is not assignable from " + component.getClass().getName());
		objectMap.put(component.getClass().getName(), component);
	}

	@SuppressWarnings("unchecked")
	public <T> Optional<T> findObject(Class<T> clazz){
		if(objectMap.containsKey(clazz.getName()))
			return (Optional<T>) Optional.of(objectMap.get(clazz.getName()));
		return Optional.empty();
	}

	public ObjectsDatabase immutable(){
		return new ImmutableObjectsDatabase(this);
	}

	private static class ImmutableObjectsDatabase extends ObjectsDatabase {

		private final ObjectsDatabase objectsDatabase;

		public ImmutableObjectsDatabase(ObjectsDatabase objectsDatabase) {
			this.objectsDatabase = objectsDatabase;
		}

		@Override
		public void addObject(Object component, Class<?> asClass) {
			throw new UnsupportedOperationException();
		}

		@Override
		public <T> Optional<T> findObject(Class<T> clazz){
			return objectsDatabase.findObject(clazz);
		}

	}

}
