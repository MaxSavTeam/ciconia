package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotation.RequestMethod;
import com.maxsavteam.ciconia.component.Controller;
import com.maxsavteam.ciconia.component.ExecutableMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MappingsContainer {

	private final CiconiaConfiguration configuration;

	private final List<Entity> entities = new ArrayList<>();

	public MappingsContainer(List<Controller> controllers, CiconiaConfiguration configuration){
		this.configuration = configuration;
		for(Controller controller : controllers)
			addController(controller);
	}

	private void addController(Controller controller){
		for(ExecutableMethod method : controller.getExecutableMethods()) {
			String fullMappingRegex;
			String controllerMapping = controller.getMappingName();
			String methodMapping = method.getMappingWrapper().getMappingName();
			if(controllerMapping.isEmpty() && methodMapping.isEmpty())
				fullMappingRegex = "";
			else if(controllerMapping.isEmpty())
				fullMappingRegex = method.getMappingWrapper().getPattern().pattern();
			else if(methodMapping.isEmpty())
				fullMappingRegex = controller.getMappingName() + configuration.getPathSeparator();
			else
				fullMappingRegex = "\\Q" + controller.getMappingName() + "\\E"
						+ "\\" + configuration.getPathSeparator()
						+ method.getMappingWrapper().getPattern().pattern();
			entities.add(new Entity(controller, method, fullMappingRegex));
		}
	}

	public MethodSearchResult findMethod(String mapping, RequestMethod requestMethod){
		for(Entity entity : entities) {
			if(entity.getMethod().getMappingWrapper().containsRequestMethod(requestMethod)){
				Matcher matcher = entity.getPattern().matcher(mapping);
				if(matcher.matches()){
					return new MethodSearchResult(
							entity.getController(),
							entity.getMethod(),
							extractPathVariables(matcher, entity.getMethod().getMappingWrapper().getPathVariables())
					);
				}
			}
		}
		return null;
	}

	private Map<String, String> extractPathVariables(Matcher matcher, List<String> variablesNames){
		Map<String, String> map = new HashMap<>();
		for(String variableName : variablesNames){
			map.put(variableName, matcher.group(variableName));
		}
		return map;
	}

	public static class MethodSearchResult {
		private final Controller controller;
		private final ExecutableMethod method;
		private final Map<String, String> pathVariablesMap;

		public MethodSearchResult(Controller controller, ExecutableMethod method, Map<String, String> pathVariablesMap) {
			this.controller = controller;
			this.method = method;
			this.pathVariablesMap = pathVariablesMap;
		}

		public Controller getController() {
			return controller;
		}

		public ExecutableMethod getMethod() {
			return method;
		}

		public Map<String, String> getPathVariablesMap() {
			return pathVariablesMap;
		}
	}

	public static class Entity {

		private final Controller controller;
		private final ExecutableMethod method;
		private final Pattern pattern;

		public Entity(Controller controller, ExecutableMethod method, String regex){
			this.controller = controller;
			this.method = method;
			pattern = Pattern.compile(regex);
		}

		public Controller getController() {
			return controller;
		}

		public ExecutableMethod getMethod() {
			return method;
		}

		public Pattern getPattern() {
			return pattern;
		}
	}

}
