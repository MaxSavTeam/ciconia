package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotation.Property;
import com.maxsavteam.ciconia.component.Component;
import com.maxsavteam.ciconia.converter.Converter;
import com.maxsavteam.ciconia.exception.IncompatibleClassException;
import com.maxsavteam.ciconia.exception.InstantiationException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class PropertyFieldsInjector {

	private final Map<String, Properties> propertiesMap = new HashMap<>();
	private final Class<?> primarySource;

	public PropertyFieldsInjector(Class<?> primarySource) {
		this.primarySource = primarySource;
	}

	public void performInjection(Component component) {
		List<Component.PropertyField> fields = component.getPropertyFields();
		for (Component.PropertyField field : fields) {
			Field f = field.getField();

			Property property = field.getProperty();
			Properties properties = getProperties(property.filename(), property.source());

			String propertyValue = properties.getProperty(property.value());
			Converter converter = new Converter(f.getGenericType(), f.getType());

			Optional<Object> op;
			if(List.class.isAssignableFrom(f.getType())){
				op = converter.convertToParameterType(Arrays.asList(propertyValue.split(",")));
			}else {
				op = converter.convertToParameterType(propertyValue);
			}
			if(!op.isPresent())
				throw new InstantiationException(new IncompatibleClassException("Can't convert property value to field type"));

			f.setAccessible(true);
			try {
				f.set(component.getClassInstance(), op.get());
			} catch (IllegalAccessException e) {
				throw new InstantiationException(e);
			}
		}
	}

	private Properties getProperties(String filename, Property.Source source){
		if(propertiesMap.containsKey(filename)){
			return propertiesMap.get(filename);
		}
		Properties properties = openProperties(filename, source);
		propertiesMap.put(filename, properties);
		return properties;
	}

	private Properties openProperties(String filename, Property.Source source){
		Properties properties = new Properties();

		try(InputStream inputStream = getInputStreamForSource(filename, source)){
			properties.load(inputStream);
		}catch (IOException e){
			throw new InstantiationException(e);
		}

		return properties;
	}

	private InputStream getInputStreamForSource(String filename, Property.Source source) throws IOException {
		if(source == Property.Source.FROM_EXTERNAL_STORAGE){
			return Files.newInputStream(Paths.get(filename));
		}
		if(source == Property.Source.FROM_RESOURCES){
			return primarySource.getClassLoader().getResourceAsStream(filename);
		}
		throw new InstantiationException("Unknown source type");
	}

}
