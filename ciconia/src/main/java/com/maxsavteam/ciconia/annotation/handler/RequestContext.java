package com.maxsavteam.ciconia.annotation.handler;

import com.maxsavteam.ciconia.component.ObjectsDatabase;
import org.json.JSONObject;

import java.util.Map;

/**
 * Contains context of individual request: request path, method mapping, immutable global objects database,
 * immutable contextual objects database, map with parameters and map with path variables
 *
 * @see ObjectsDatabase
 * @author Max Savitsky
 */
public class RequestContext {

	private String methodDeclarationPath;
	private String methodMapping;
	private ObjectsDatabase objectsDatabase;
	private ObjectsDatabase contextualObjectsDatabase;
	private Map<String, Object> parameters;
	private JSONObject parametersJsonObject;
	private Map<String, String> pathVariables;

	/**
	 * Method declaration path is concatenation of containing class name (with package), sharp symbol and method name.
	 * For example, {@code package.Sample#method}
	 *
	 * @return method declaration path
	 */
	public String getMethodDeclarationPath() {
		return methodDeclarationPath;
	}

	public RequestContext setMethodDeclarationPath(String methodDeclarationPath) {
		this.methodDeclarationPath = methodDeclarationPath;
		return this;
	}

	/**
	 * @return full method mapping (controller mapping + path separator + method mapping)
	 */
	public String getMethodMapping() {
		return methodMapping;
	}

	public RequestContext setMethodMapping(String methodMapping) {
		this.methodMapping = methodMapping;
		return this;
	}

	/**
	 * @return immutable global objects database
	 * @see ObjectsDatabase
	 */
	public ObjectsDatabase getObjectsDatabase() {
		return objectsDatabase;
	}

	public RequestContext setObjectsDatabase(ObjectsDatabase objectsDatabase) {
		this.objectsDatabase = objectsDatabase;
		return this;
	}

	/**
	 * @return immutable contextual objects database
	 * @see ObjectsDatabase
	 */
	public ObjectsDatabase getContextualObjectsDatabase() {
		return contextualObjectsDatabase;
	}

	public RequestContext setContextualObjectsDatabase(ObjectsDatabase contextualObjectsDatabase) {
		this.contextualObjectsDatabase = contextualObjectsDatabase;
		return this;
	}

	/**
	 * @return map with request parameters
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	public RequestContext setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
		return this;
	}

	/**
	 * @return JSON object with request parameters
	 * */
	public JSONObject getParametersJsonObject() {
		return parametersJsonObject;
	}

	public RequestContext setParametersJsonObject(JSONObject parametersJsonObject) {
		this.parametersJsonObject = parametersJsonObject;
		return this;
	}

	/**
	 * @return map with resolved path variables
	 * @see <a href="https://ciconia.maxsavteam.com/docs/#/components/controllers?id=path-variables">Path variables documentation</a>
	 */
	public Map<String, String> getPathVariables() {
		return pathVariables;
	}

	public RequestContext setPathVariables(Map<String, String> pathVariables) {
		this.pathVariables = pathVariables;
		return this;
	}
}
