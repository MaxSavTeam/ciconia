package com.maxsavteam.ciconia.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxsavteam.ciconia.exception.IncompatibleClassException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Object converter to specific type.
 * @author Max Savitsky
 * */
public class Converter {

	/**
	 * Using to indicate, that {@link #convertToParameterType(Object)} returns {@code null} value.
	 * */
	public static final Object NULL_VALUE = new Object();

	private final Type type;
	private final Class<?> clazz;

	public Converter(Type type, Class<?> clazz) {
		this.type = type;
		this.clazz = clazz;
	}

	/**
	 * Converts given object to specified type.
	 * @param param object to convert
	 * @return {@link Optional} with converted object or {@link Optional#empty()} if conversion is not possible
	 * */
	public Optional<Object> convertToParameterType(Object param){
		if(param == null)
			return Optional.of(NULL_VALUE);
		if(clazz.isArray() && (param instanceof List || param instanceof JSONArray)){
			Class<?> type = clazz.getComponentType();

			Converter converter = new Converter(type, type);

			List<?> list;
			if(param instanceof JSONArray)
				list = toList((JSONArray)param);
			else
				list = (List<?>) param;
			Object array = Array.newInstance(type, list.size());
			for(int i = 0; i < list.size(); i++){
				Object o = list.get(i);
				Optional<Object> converted = converter.convertToParameterType(o);
				if(converted.isPresent()){
					Array.set(array, i, converted.get());
				}else{
					return Optional.empty();
				}
			}

			return Optional.of(array);
		}
		if(List.class.equals(clazz) && (param instanceof List || param instanceof JSONArray)){
			Object paramList;
			if(param instanceof JSONArray){
				paramList = toList((JSONArray) param);
			}else{
				paramList = param;
			}
			return convertLists(paramList);
		}
		if(clazz.isAssignableFrom(param.getClass()))
			return Optional.of(param);
		if(param instanceof JSONObject){
			try {
				return Optional.of(new ObjectMapper().readValue(param.toString(), clazz));
			} catch (JsonProcessingException e) {
				throw new IncompatibleClassException(e);
			}
		}

		if(param instanceof String){
			Object result = StringConverter.tryToConvert((String) param, clazz);
			if(result != null)
				return Optional.of(result);
		}

		return Optional.empty();
	}

	private Optional<Object> convertLists(Object param){
		Class<?> currentGenericType;
		Type genericType;
		if(clazz.getGenericSuperclass() != null)
			genericType = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
		else
			genericType = ((ParameterizedType) type).getActualTypeArguments()[0];

		if (genericType instanceof Class) {
			currentGenericType = (Class<?>) genericType;
		} else {
			return Optional.empty();
		}

		if(param instanceof List){
			Converter converter = new Converter(genericType, currentGenericType);
			List<Object> list = new ArrayList<>();
			List<?> paramList = (List<?>) param;
			for (Object item : paramList) {
				Optional<Object> result = converter.convertToParameterType(item);
				if (!result.isPresent())
					return Optional.empty();
				list.add(result.get());
			}
			return Optional.of(list);
		}
		return Optional.empty();
	}

	/**
	 * @return type of parameter which this converter associated with.
	 * */
	public Type getParameterType() {
		return type;
	}

	/**
	 * @return class of parameter which this converter associated with.
	 * */
	public Class<?> getParameterClass() {
		return clazz;
	}

	private static List<Object> toList(JSONArray jsonArray){
		List<Object> list = new ArrayList<>(jsonArray.length());
		for(int i = 0; i < jsonArray.length(); i++){
			list.add(jsonArray.get(i));
		}
		return list;
	}
}
