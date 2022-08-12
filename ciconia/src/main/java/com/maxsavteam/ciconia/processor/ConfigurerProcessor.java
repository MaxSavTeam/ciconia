package com.maxsavteam.ciconia.processor;

import com.maxsavteam.ciconia.annotation.ObjectFactory;
import com.maxsavteam.ciconia.component.Configurer;
import com.maxsavteam.ciconia.component.ObjectFactoryMethod;
import com.maxsavteam.ciconia.exception.IllegalObjectFactoryMethodDeclarationException;
import com.maxsavteam.ciconia.exception.InvalidConfigurationDeclarationException;
import com.maxsavteam.ciconia.exception.MethodNotAccessibleException;
import com.maxsavteam.ciconia.utils.CiconiaUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ConfigurerProcessor {

	public List<Configurer> process(List<Class<?>> classes) {
		List<Configurer> configurers = new ArrayList<>();
		for (Class<?> clazz : classes) {
			configurers.add(process(clazz));
		}
		return configurers;
	}

	public Configurer process(Class<?> cl) {
		List<ObjectFactoryMethod> methods = new ArrayList<>();
		int classModifiers = cl.getModifiers();
		if (Modifier.isInterface(classModifiers))
			throw new InvalidConfigurationDeclarationException("Configuration cannot be interface");
		if (Modifier.isAbstract(classModifiers))
			throw new InvalidConfigurationDeclarationException("Configuration cannot be abstract class");

		if (!checkIfClassCanBeAccessedAsStatic(cl))
			throw new InvalidConfigurationDeclarationException("Configuration should be static or top-level public class");

		for (Method method : cl.getDeclaredMethods()) {
			if (!method.isAnnotationPresent(ObjectFactory.class))
				continue;

			int modifiers = method.getModifiers();
			if (Modifier.isStatic(modifiers))
				throw new IllegalObjectFactoryMethodDeclarationException("Object factory method cannot be static");
			if(!Modifier.isPublic(modifiers))
				throw new IllegalObjectFactoryMethodDeclarationException("Object factory method must be public");

			Class<?> returnType = method.getReturnType();
			if (returnType == Void.TYPE || returnType.isPrimitive())
				throw new IllegalObjectFactoryMethodDeclarationException("Method should not return void or primitive type");
			if (!method.trySetAccessible())
				throw new MethodNotAccessibleException("Method is not accessible: " + CiconiaUtils.getMethodDeclarationString(method));
			methods.add(new ObjectFactoryMethod(method));
		}

		return new Configurer(cl, methods);
	}

	/**
	 * Returns true if class is top-level class or can be accessed as static.<br>
	 * For example
	 * <pre>{@code
	 * class Test1 {
	 *      static class Test2 {
	 *          static class Test3 {
	 *
	 *          }
	 *      }
	 * }
	 * }</pre>
	 * class Test3 can be accessed, but now cannot, because Test2 is not static or top-level
	 * <pre>{@code
	 * 	class Test1 {
	 *      class Test2 {
	 *          static class Test3 {
	 *
	 *          }
	 *      }
	 * }
	 * }</pre>
	 */
	private boolean checkIfClassCanBeAccessedAsStatic(Class<?> cl) {
		int modifiers = cl.getModifiers();
		if(!Modifier.isPublic(modifiers))
			return false;
		Class<?> declaringClass = cl.getDeclaringClass();
		if (declaringClass == null)
			return true;
		return Modifier.isStatic(modifiers) && checkIfClassCanBeAccessedAsStatic(declaringClass);
	}

}
