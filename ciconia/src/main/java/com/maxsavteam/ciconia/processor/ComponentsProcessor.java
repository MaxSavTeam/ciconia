package com.maxsavteam.ciconia.processor;

import com.maxsavteam.ciconia.annotation.Cron;
import com.maxsavteam.ciconia.component.Component;
import com.maxsavteam.ciconia.exception.IllegalMethodDeclaration;
import com.maxsavteam.ciconia.exception.InvalidComponentDeclarationException;
import com.maxsavteam.ciconia.exception.InvalidCronExpressionException;
import com.maxsavteam.ciconia.utils.CiconiaUtils;
import org.quartz.CronExpression;

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
		}
	}

	private void processCronMethods(Component component){
		List<Component.CronMethod> cronMethods = new ArrayList<>();
		for(Method method : component.getaClass().getDeclaredMethods()){
			Cron cron = method.getAnnotation(Cron.class);
			if(cron == null)
				continue;

			CiconiaUtils.checkMethodDeclaration(method, IllegalMethodDeclaration.class);

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
