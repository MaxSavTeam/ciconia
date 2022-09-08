package com.maxsavteam.ciconia.component;

import com.maxsavteam.ciconia.utils.CiconiaUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Contains information about post-initialization method.
 * @author Max Savitsky
 * */
public class PostInitializationMethod {

	private final Method method;

	public PostInitializationMethod(Method method) {
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

	public void invoke(ObjectsDatabase objectsDatabase) throws InvocationTargetException, IllegalAccessException {
		Object[] arguments = new Object[method.getParameterCount()];
		for(int i = 0; i < arguments.length; i++){
			arguments[i] = objectsDatabase
					.findObject( method.getParameterTypes()[i] )
					.orElse(null);
		}
		Optional<?> parent = objectsDatabase.findObject(method.getDeclaringClass());
		if(parent.isEmpty())
			throw new IllegalStateException("Could not find parent object for post initialization method: " + CiconiaUtils.getMethodDeclarationString(method));
		method.invoke(parent.get(), arguments);
	}

}
