package com.maxsavteam.ciconia.annotation.handler;

import com.maxsavteam.ciconia.component.ObjectsDatabase;

import java.util.Map;

/**
 * Contains context of individual request: request path, method mapping, immutable global objects database,
 * immutable contextual objects database, map with parameters and map with path variables
 * @see ObjectsDatabase
 * */
public class RequestContext {

	private String methodDeclarationPath;
	private String methodMapping;
	private ObjectsDatabase objectsDatabase;
	private ObjectsDatabase contextualObjectsDatabase;
	private Map<String, Object> parameters;
	private Map<String, String> pathVariables;

	public String getMethodDeclarationPath() {
		return methodDeclarationPath;
	}

	public RequestContext setMethodDeclarationPath(String methodDeclarationPath) {
		this.methodDeclarationPath = methodDeclarationPath;
		return this;
	}

	public String getMethodMapping() {
		return methodMapping;
	}

	public RequestContext setMethodMapping(String methodMapping) {
		this.methodMapping = methodMapping;
		return this;
	}

	public ObjectsDatabase getObjectsDatabase() {
		return objectsDatabase;
	}

	public RequestContext setObjectsDatabase(ObjectsDatabase objectsDatabase) {
		this.objectsDatabase = objectsDatabase;
		return this;
	}

	public ObjectsDatabase getContextualObjectsDatabase() {
		return contextualObjectsDatabase;
	}

	public RequestContext setContextualObjectsDatabase(ObjectsDatabase contextualObjectsDatabase) {
		this.contextualObjectsDatabase = contextualObjectsDatabase;
		return this;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public RequestContext setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
		return this;
	}

	public Map<String, String> getPathVariables() {
		return pathVariables;
	}

	public RequestContext setPathVariables(Map<String, String> pathVariables) {
		this.pathVariables = pathVariables;
		return this;
	}
}
