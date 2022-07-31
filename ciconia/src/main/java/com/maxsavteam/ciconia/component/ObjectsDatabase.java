package com.maxsavteam.ciconia.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ObjectsDatabase {

	private final Map<String, Object> objectMap = new HashMap<>();

	public void addObject(Object component){
		objectMap.put(component.getClass().getName(), component);
	}

	@SuppressWarnings("unchecked")
	public <T> Optional<T> findObject(Class<T> clazz){
		if(objectMap.containsKey(clazz.getName()))
			return (Optional<T>) Optional.of(objectMap.get(clazz.getName()));
		return Optional.empty();
	}

	public <T> Optional<T> findSuitableObject(Class<T> clazz){
		Optional<T> op = findObject(clazz);
		if(op.isPresent())
			return op;
		@SuppressWarnings("unchecked")
		Optional<T> t = (Optional<T>) objectMap.values()
				.stream()
				.filter(entry -> clazz.isAssignableFrom(entry.getClass()))
				.findFirst();
		return t;
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
		public void addObject(Object component) {
			throw new UnsupportedOperationException();
		}

		@Override
		public <T> Optional<T> findObject(Class<T> clazz){
			return objectsDatabase.findObject(clazz);
		}

		@Override
		public <T> Optional<T> findSuitableObject(Class<T> clazz){
			return objectsDatabase.findSuitableObject(clazz);
		}

	}

}
