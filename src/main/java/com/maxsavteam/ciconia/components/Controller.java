package com.maxsavteam.ciconia.components;

import javax.swing.text.html.Option;
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

	public Optional<ExecutableMethod> findMethodByMapping(String mappingName){
		for(ExecutableMethod method : executableMethods){
			if(method.getMappingName().equals(mappingName))
				return Optional.of(method);
		}
		return Optional.empty();
	}

}
