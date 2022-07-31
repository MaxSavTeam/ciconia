package com.maxsavteam.ciconia.parser;

import com.maxsavteam.ciconia.component.Component;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ComponentsParser {

	public static List<Component> parseComponents(Class<?> source){
		Set<Class<?>> set = new Reflections(source.getPackageName()).get(Scanners.SubTypes.of(Scanners.TypesAnnotated.with(com.maxsavteam.ciconia.annotation.Component.class)).asClass());
		List<Component> components = new ArrayList<>();
		for(Class<?> cl : set){
			components.add(new Component(cl));
		}
		return components;
	}

}
