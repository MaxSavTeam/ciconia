package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.components.Component;
import com.maxsavteam.ciconia.components.ComponentsDatabase;
import com.maxsavteam.ciconia.components.Controller;
import com.maxsavteam.ciconia.components.ExecutableMethod;
import com.maxsavteam.ciconia.exceptions.DuplicateMappingException;
import com.maxsavteam.ciconia.exceptions.InstantiationException;
import com.maxsavteam.ciconia.graph.ComponentsDependenciesGraph;
import com.maxsavteam.ciconia.tree.Tree;
import com.maxsavteam.ciconia.tree.TreeBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CiconiaApplication {

	private final Class<?> primarySource;

	public CiconiaApplication(Class<?> primarySource) {
		this.primarySource = primarySource;
	}

	private void run(){
		List<Component> components = new Parser(primarySource).parse();
		List<Controller> controllers = components
				.stream()
				.filter(Controller.class::isInstance)
				.map(Controller.class::cast)
				.collect(Collectors.toList());

		requireNoDuplicateMappings(controllers);

		ComponentsDependenciesGraph graph = new ComponentsDependenciesGraph(components);
		List<Component> cycle = graph.findDependencyCycle();
		if(!cycle.isEmpty()){
			String cycleString = getPrettyCycle(cycle);
			throw new InstantiationException("Dependency cycle found\n" + cycleString);
		}

		List<Component> topologicalOrder = graph.getInTopologicalOrder();

		ComponentsDatabase componentsDatabase
				= InstantiationUtils.instantiateComponents(topologicalOrder);

		Tree tree = TreeBuilder.build(controllers);
		CiconiaHandler.initialize(tree, componentsDatabase);
	}

	private String getPrettyCycle(List<Component> cycle){
		StringBuilder sb = new StringBuilder();
		sb.append("┌─────┐\n");
		for(int i = 0; i < cycle.size(); i++){
			Class<?> cl = cycle.get(i).getComponentClass();
			sb.append("|  ").append(cl.getSimpleName())
					.append(" (").append(cl.getName()).append(")")
					.append("\n");
			if(i != cycle.size() - 1){
				sb.append(String.format("↑     ↓%n"));
			}
		}
		sb.append("└─────").append((char) 217); // symbol does not work
		return sb.toString();
	}

	private static void requireNoDuplicateMappings(List<Controller> controllers){
		Map<String, String> map = new HashMap<>();
		for(Controller controller : controllers){
			for(ExecutableMethod method : controller.getExecutableMethods()){
				String path = controller.getComponentClass().getName() + "#" + method.getMethod().getName();
				String mapping = controller.getMappingName() + "." + method.getMappingName();
				if(map.containsKey(mapping))
					throw new DuplicateMappingException(map.get(mapping) + " and " + path + " have the same mapping (" + mapping + ")");
				map.put(mapping, path);
			}
		}
	}

	public static void run(Class<?> source){
		new CiconiaApplication(source).run();
	}

}
