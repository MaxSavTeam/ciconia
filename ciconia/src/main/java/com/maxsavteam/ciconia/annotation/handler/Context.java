package com.maxsavteam.ciconia.annotation.handler;

import com.maxsavteam.ciconia.component.ObjectsDatabase;

import java.util.Map;

/**
 * Contains context of individual request: request path, method mapping, immutable global objects database,
 * immutable contextual objects database, map with parameters and map with path variables
 * @see ObjectsDatabase
 * */
public class Context {

	private String methodPath;
	private String methodMapping;
	private ObjectsDatabase componentsDatabase;
	private ObjectsDatabase contextualObjectsDatabase;
	private Map<String, Object> parameters;
	private Map<String, String> pathVariables;

	public String getMethodPath() {
		return methodPath;
	}

	public Context setMethodPath(String methodPath) {
		this.methodPath = methodPath;
		return this;
	}

	public String getMethodMapping() {
		return methodMapping;
	}

	public Context setMethodMapping(String methodMapping) {
		this.methodMapping = methodMapping;
		return this;
	}

	public ObjectsDatabase getComponentsDatabase() {
		return componentsDatabase;
	}

	public Context setComponentsDatabase(ObjectsDatabase componentsDatabase) {
		this.componentsDatabase = componentsDatabase;
		return this;
	}

	public ObjectsDatabase getContextualObjectsDatabase() {
		return contextualObjectsDatabase;
	}

	public Context setContextualObjectsDatabase(ObjectsDatabase contextualObjectsDatabase) {
		this.contextualObjectsDatabase = contextualObjectsDatabase;
		return this;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public Context setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
		return this;
	}

	public Map<String, String> getPathVariables() {
		return pathVariables;
	}

	public Context setPathVariables(Map<String, String> pathVariables) {
		this.pathVariables = pathVariables;
		return this;
	}
}
