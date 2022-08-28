package com.maxsavteam.ciconia.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxsavteam.ciconia.exception.IncompatibleClassException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Attempts to convert object to specific type.
 * @author Max Savitsky
 * */
public class Converter {

	public static final Object NULL_VALUE = new Object();

	private final Type type;
	private final Class<?> clazz;

	public Converter(Type type, Class<?> clazz) {
		this.type = type;
		this.clazz = clazz;
	}

	public Optional<Object> convertToParameterType(Object param){
		if(param == null)
			return Optional.of(NULL_VALUE);
		if(List.class.equals(clazz) && (param instanceof List || param instanceof JSONArray)){
			Object paramList;
			if(param instanceof JSONArray){
				JSONArray array = (JSONArray) param;
				List<Object> list = new ArrayList<>();
				for(int i = 0; i < array.length(); i++){
					list.add(array.get(i));
				}
				paramList = list;
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
				if (result.isEmpty())
					return Optional.empty();
				list.add(result.get());
			}
			return Optional.of(list);
		}
		return Optional.empty();
	}

}
