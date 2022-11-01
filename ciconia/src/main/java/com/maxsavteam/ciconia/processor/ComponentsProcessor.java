package com.maxsavteam.ciconia.processor;

import com.maxsavteam.ciconia.annotation.Cron;
import com.maxsavteam.ciconia.annotation.Property;
import com.maxsavteam.ciconia.component.Component;
import com.maxsavteam.ciconia.exception.InvalidFieldDeclaration;
import com.maxsavteam.ciconia.exception.InvalidMethodDeclaration;
import com.maxsavteam.ciconia.exception.InvalidComponentDeclarationException;
import com.maxsavteam.ciconia.exception.InvalidCronExpressionException;
import com.maxsavteam.ciconia.utils.CiconiaUtils;
import org.quartz.CronExpression;

import javax.annotation.PreDestroy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ComponentsProcessor {

	public Component process(Class<?> cl){
		int modifiers = cl.getModifiers();

		if(Modifier.isAbstract(modifiers))
			throw new InvalidComponentDeclarationException("Component cannot be abstract class");

		if(Modifier.isInterface(modifiers))
			throw new InvalidComponentDeclarationException("Component cannot be interface");

		if(!Modifier.isPublic(modifiers) || cl.getDeclaringClass() != null)
			throw new InvalidComponentDeclarationException("Component should be public top-level class");
		return new Component(cl);
	}

	public void setup(List<Component> components){
		for(Component component : components){
			processCronMethods(component);
			processProperties(component);
			processPostConstructMethods(component);
			processPreDestroyMethods(component);
		}
	}

	private void processPreDestroyMethods(Component component){
		List<Method> preDestroyMethods = new ArrayList<>();
		for(Method method : component.getaClass().getDeclaredMethods()){
			if(method.isAnnotationPresent(PreDestroy.class)){
				if(method.getParameterCount() != 0)
					throw new InvalidMethodDeclaration("PreDestroy method cannot have parameters");
				CiconiaUtils.checkMethodDeclaration(method, InvalidMethodDeclaration.class, "PreDestroy");
				preDestroyMethods.add(method);
			}
		}
		component.setPreDestroyMethods(preDestroyMethods);
	}

	private void processPostConstructMethods(Component component){
		List<Method> postConstructMethods = new ArrayList<>();
		for(Method method : component.getaClass().getDeclaredMethods()){
			if(method.isAnnotationPresent(javax.annotation.PostConstruct.class)){
				if(method.getParameterCount() != 0)
					throw new InvalidMethodDeclaration("PostConstruct method cannot have parameters");
				CiconiaUtils.checkMethodDeclaration(method, InvalidMethodDeclaration.class, "PostConstruct");
				postConstructMethods.add(method);
			}
		}
		component.setPostConstructMethods(postConstructMethods);
	}

	private void processProperties(Component component){
		List<Component.PropertyField> propertyFields = new ArrayList<>();
		Field[] fields = component.getaClass().getDeclaredFields();
		for(Field field : fields){
			Property property = field.getAnnotation(Property.class);
			if(property == null)
				continue;
			int modifiers = field.getModifiers();
			if(Modifier.isStatic(modifiers)
				|| Modifier.isFinal(modifiers))
				throw new InvalidFieldDeclaration("Property field cannot be static or final");
			propertyFields.add(new Component.PropertyField(property, field));
		}
		component.setPropertyFields(propertyFields);
	}

	private void processCronMethods(Component component){
		List<Component.CronMethod> cronMethods = new ArrayList<>();
		for(Method method : component.getaClass().getDeclaredMethods()){
			Cron cron = method.getAnnotation(Cron.class);
			if(cron == null)
				continue;

			CiconiaUtils.checkMethodDeclaration(method, InvalidMethodDeclaration.class, "Cron");

			validateCronExpression(cron.value());

			cronMethods.add(new Component.CronMethod(cron, method));
		}
		component.setCronMethods(cronMethods);
	}

	private void validateCronExpression(String expression){
		try {
			new CronExpression(expression);
		} catch (ParseException e) {
			throw new InvalidCronExpressionException(e);
		}
	}

}
