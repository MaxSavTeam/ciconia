package com.maxsavteam.ciconia.component;

import java.util.List;

public class Controller extends Component {
	private final String mappingName;
	private final List<ExecutableMethod> executableMethods;

	public Controller(Class<?> controllerClass, String mappingName, List<ExecutableMethod> executableMethods) {
		super(controllerClass);
		this.mappingName = mappingName;
		this.executableMethods = executableMethods;
	}

	public String getMappingName() {
		return mappingName;
	}

	public List<ExecutableMethod> getExecutableMethods() {
		return executableMethods;
	}

}
