package com.maxsavteam.ciconia.component;

import com.maxsavteam.ciconia.annotation.Cron;
import com.maxsavteam.ciconia.annotation.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents particular component class.
 * @author Max Savitsky
 * */
public class Component extends InstantiatableComponent {

	private List<CronMethod> cronMethods = new ArrayList<>();
	private List<PropertyField> propertyFields = new ArrayList<>();

	public Component(Class<?> aClass) {
		super(aClass);
	}

	public void setCronMethods(List<CronMethod> cronMethods) {
		this.cronMethods = cronMethods;
	}

	public List<CronMethod> getCronMethods() {
		return cronMethods;
	}

	public List<PropertyField> getPropertyFields() {
		return propertyFields;
	}

	public void setPropertyFields(List<PropertyField> propertyFields) {
		this.propertyFields = propertyFields;
	}

	public static class CronMethod {

		private final Cron cron;
		private final Method method;

		public CronMethod(Cron cron, Method method) {
			this.cron = cron;
			this.method = method;
		}

		public Cron getCron() {
			return cron;
		}

		public Method getMethod() {
			return method;
		}
	}

	public static class PropertyField {

		private final Property property;
		private final Field field;

		public PropertyField(Property property, Field field) {
			this.property = property;
			this.field = field;
		}

		public Property getProperty() {
			return property;
		}

		public Field getField() {
			return field;
		}
	}

}
