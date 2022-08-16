package com.maxsavteam.ciconia.utils;

import com.maxsavteam.ciconia.component.InstantiatableObject;
import com.maxsavteam.ciconia.component.ObjectFactoryMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CiconiaUtils {

	public static String getMethodDeclarationString(Method method){
		Class<?> declaringClass = method.getDeclaringClass();
		return declaringClass.getName() + "#" + method.getName();
	}

	public static String getPrettyCycle(List<InstantiatableObject> cycle) {
		List<InstantiatableObject> list = new ArrayList<>(cycle);
		list.add(list.get(0));

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			String name;
			InstantiatableObject object = list.get(i);
			if(object instanceof ObjectFactoryMethod){
				ObjectFactoryMethod method = (ObjectFactoryMethod) object;
				name = String.format(
						"Object factory method '%s' in %s",
						method.getMethod().getName(),
						method.getMethod().getDeclaringClass().getName()
				);
			} else{
				Class<?> cl = object.getaClass();
				name = String.format(
						"Component '%s' (%s)",
						cl.getSimpleName(),
						cl.getName()
				);
			}
			sb.append(name).append("\n")
					.append(" V").append("\n");
			if (i == list.size() - 1) {
				sb.append("...");
			}
		}
		return sb.toString();
	}
}
