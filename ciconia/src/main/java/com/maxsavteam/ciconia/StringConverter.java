package com.maxsavteam.ciconia;

import org.json.JSONException;
import org.json.JSONObject;

class StringConverter {

	public static Object tryToConvert(String param, Class<?> clazz) {
		if(clazz.isAssignableFrom(String.class))
			return param;

		if(clazz.isAssignableFrom(Integer.class) && isInteger(param))
			return Integer.parseInt(param);

		if(clazz.isAssignableFrom(Long.class) && isLong(param))
			return Long.parseLong(param);

		if(clazz.isAssignableFrom(Double.class) && isDouble(param))
			return Double.parseDouble(param);

		if(clazz.isAssignableFrom(Float.class) && isFloat(param))
			return Float.parseFloat(param);

		if(clazz.isAssignableFrom(Boolean.class) && isBoolean(param))
			return Boolean.parseBoolean(param);

		if(clazz.isAssignableFrom(JSONObject.class) && isJson(param))
			return new JSONObject(param);

		return null;
	}

	private interface Checker {
		Object check(String param);
	}

	private static boolean isInteger(String str){
		return checkNumber(str, Integer::parseInt);
	}

	private static boolean isLong(String str){
		return checkNumber(str, Long::parseLong);
	}

	private static boolean isDouble(String str){
		return checkNumber(str, Double::parseDouble);
	}

	private static boolean isFloat(String str){
		return checkNumber(str, Float::parseFloat);
	}

	private static boolean isBoolean(String str){
		return checkNumber(str, Boolean::parseBoolean);
	}

	private static boolean isJson(String str){
		try {
			new JSONObject(str);
			return true;
		} catch (JSONException e) {
			return false;
		}
	}

	private static boolean checkNumber(String str, Checker checker){
		try {
			checker.check(str);
			return true;
		}catch (NumberFormatException e){
			return false;
		}
	}

}
