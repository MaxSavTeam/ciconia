package com.maxsavteam.ciconia.components;

import com.maxsavteam.ciconia.annotation.RequestMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

	public Optional<ExecutableMethod> findMethodByMapping(String mappingName, RequestMethod requestMethod){
		return executableMethods
				.stream()
				.filter(
						m -> m.getMappingName().equals(mappingName)
								&& Arrays.asList(m.getMapping().method()).contains(requestMethod)
				)
				.findFirst();
	}

}
