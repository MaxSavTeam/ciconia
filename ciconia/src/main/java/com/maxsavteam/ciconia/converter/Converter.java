package com.maxsavteam.ciconia.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxsavteam.ciconia.exception.IncompatibleClassException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Converter {

	public static final Object NULL_VALUE = new Object();

	public static Optional<Object> convertToParameterType(Object param, Class<?> cl){
		if(param == null)
			return Optional.of(NULL_VALUE);
		if(cl.isAssignableFrom(param.getClass()))
			return Optional.of(param);
		if(param instanceof JSONObject){
			try {
				return Optional.of(new ObjectMapper().readValue(param.toString(), cl));
			} catch (JsonProcessingException e) {
				throw new IncompatibleClassException(e);
			}
		}
		if(cl.isAssignableFrom(List.class) && param instanceof JSONArray){
			List<Object> list = new ArrayList<>();
			JSONArray jsonArray = (JSONArray) param;
			for(int i = 0; i < jsonArray.length(); i++){
				list.add(jsonArray.get(i));
			}
			return Optional.of(list);
		}

		if(param instanceof String){
			Object result = StringConverter.tryToConvert((String) param, cl);
			if(result != null)
				return Optional.of(result);
		}

		return Optional.empty();
	}

}
