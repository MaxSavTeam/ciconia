package com.maxsavteam.ciconia.parser;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ComponentsParser {

	public static List<Class<?>> parse(Class<?> source){
		Set<Class<?>> set = new Reflections(source.getPackageName()).get(Scanners.SubTypes.of(Scanners.TypesAnnotated.with(com.maxsavteam.ciconia.annotation.Component.class)).asClass());
		return new ArrayList<>(set);
	}

}
