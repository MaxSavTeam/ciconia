package com.maxsavteam.ciconia.utils;

import java.lang.reflect.Method;

public class CiconiaUtils {

	public static String getMethodDeclarationString(Method method){
		Class<?> declaringClass = method.getDeclaringClass();
		return declaringClass.getName() + "#" + method.getName();
	}

}
