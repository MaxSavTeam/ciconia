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
		Optional<Object> op = findObject(clazz);
		if(op.isPresent())
			return op;
		return objectMap.values()
				.stream()
				.filter(entry -> clazz.isAssignableFrom(entry.getClass()))
				.findFirst();
	}

	public static ObjectsDatabase immutable(ObjectsDatabase database){
		return new UnmodifiedObjectsDatabase(database);
	}

	private static class UnmodifiedObjectsDatabase extends ObjectsDatabase {

		private final ObjectsDatabase objectsDatabase;

		public UnmodifiedObjectsDatabase(ObjectsDatabase objectsDatabase) {
			this.objectsDatabase = objectsDatabase;
		}

		@Override
		public void addObject(Object object) {
			throw new UnsupportedOperationException();
		}

		public Optional<Object> findObject(Class<?> clazz){
			return objectsDatabase.findObject(clazz);
		}

		public Optional<Object> findSuitableObject(Class<?> clazz){
			return objectsDatabase.findSuitableObject(clazz);
		}

	}

}
