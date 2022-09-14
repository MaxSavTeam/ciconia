package com.maxsavteam.ciconia.component;

import com.maxsavteam.ciconia.InstantiatableObjectFactory;
import com.maxsavteam.ciconia.exception.InstantiationException;
import com.maxsavteam.ciconia.utils.CiconiaUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Represents object factory method from configuration as an individual object, separate from the declaring configuration class.
 * @author Max Savitsky
 */
public class ObjectFactoryMethod extends InstantiatableObject {

	private final Method method;
	private final List<Class<?>> parameters;
	private final Class<?> objectClass;

	public ObjectFactoryMethod(Method method) {
		super(method.getReturnType());
		this.method = method;
		this.objectClass = method.getReturnType();
		this.parameters = Arrays.asList(method.getParameterTypes());
	}

	@Override
	public List<Class<?>> getDependenciesClasses() {
		List<Class<?>> list = new ArrayList<>();
		list.add(method.getDeclaringClass()); // method have to declare dependency on containing class
		list.addAll(parameters);
		return list;
	}

	public Method getMethod() {
		return method;
	}

	public List<Class<?>> getParameters() {
		return parameters;
	}

	public Class<?> getObjectClass() {
		return objectClass;
	}

	@Override
	public InstantiatableObjectFactory getFactory() {
		return database -> {
			Optional<?> op = database.findObject(method.getDeclaringClass());
			if (!op.isPresent()) {
				throw new InstantiationException(
						"Could not find object required for object factory method execution: "
								+ CiconiaUtils.getMethodDeclarationString(method)
				);
			}
			Object object = op.get();
			Object[] parameters = new Object[this.parameters.size()];
			for (int i = 0; i < parameters.length; i++) {
				op = database.findObject(this.parameters.get(i));
				if (!op.isPresent()) {
					throw new InstantiationException(
							String.format(
									"Parameter %d in %s require object of type %s that could not be found",
									i, method.getName(),
									method.getDeclaringClass().getName()
							)
					);
				}
				parameters[i] = op.get();
			}
			try {
				return method.invoke(object, parameters);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new InstantiationException(e);
			}
		};
	}
}
