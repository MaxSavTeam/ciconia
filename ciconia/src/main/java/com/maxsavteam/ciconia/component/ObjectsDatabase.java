package com.maxsavteam.ciconia.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ObjectsDatabase {

	private final Map<String, Object> objectMap = new HashMap<>();

	public void addObject(Object object){
		objectMap.put(object.getClass().getName(), object);
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

		public <T> Optional<T> findObject(Class<T> clazz){
			return objectsDatabase.findObject(clazz);
		}

		public <T> Optional<T> findSuitableObject(Class<T> clazz){
			return objectsDatabase.findSuitableObject(clazz);
		}

	}

}
