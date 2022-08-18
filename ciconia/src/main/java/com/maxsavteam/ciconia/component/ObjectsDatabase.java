package com.maxsavteam.ciconia.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ObjectsDatabase {

	private final Map<String, Object> objectMap = new HashMap<>();

	/**
	 * Stores an object in the database under given class, even if component class and given class are not the same.
	 * @param component Object to store.
	 * @param asClass Class under which object should be stored.
	 * @throws IllegalStateException if class is not assignable from object class
	 * */
	public void addObject(Object component, Class<?> asClass){
		if(!asClass.isAssignableFrom(component.getClass()))
			throw new IllegalStateException("Class " + asClass.getName() + " is not assignable from " + component.getClass().getName());
		objectMap.put(component.getClass().getName(), component);
	}

	/**
	 * @param <T> Type of the object to get.
	 * @param clazz Class of the object to get.
	 * @return object of given class if it is present in the database.
	 * */
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
