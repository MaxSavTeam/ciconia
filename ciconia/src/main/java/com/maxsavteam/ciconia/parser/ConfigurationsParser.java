package com.maxsavteam.ciconia.parser;

import com.maxsavteam.ciconia.annotation.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConfigurationsParser {

	public static List<Class<?>> parse(Class<?> source) {
		Set<Class<?>> set = new Reflections(source.getPackage().getName())
				.get(Scanners.SubTypes.of(
						Scanners.TypesAnnotated.with(Configuration.class)
				).asClass());
		return new ArrayList<>(set);
	}

}
