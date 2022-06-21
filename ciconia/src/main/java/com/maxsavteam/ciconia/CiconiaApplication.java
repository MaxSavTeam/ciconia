package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotation.RequestMethod;
import com.maxsavteam.ciconia.annotation.ValueConstants;
import com.maxsavteam.ciconia.exception.DuplicateMappingException;
import com.maxsavteam.ciconia.graph.ComponentsDependenciesGraph;
import com.maxsavteam.ciconia.tree.Tree;
import com.maxsavteam.ciconia.tree.TreeBuilder;
import com.maxsavteam.ciconia.component.Component;
import com.maxsavteam.ciconia.component.ComponentsDatabase;
import com.maxsavteam.ciconia.component.Controller;
import com.maxsavteam.ciconia.component.ExecutableMethod;
import com.maxsavteam.ciconia.exception.InstantiationException;
import com.maxsavteam.ciconia.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CiconiaApplication {

	private final Class<?> primarySource;
	private final CiconiaConfiguration configuration;

	private CiconiaApplication(Class<?> primarySource, CiconiaConfiguration configuration) {
		this.primarySource = primarySource;
		this.configuration = configuration;
	}

	private void run(){
		List<Component> components = new Parser(primarySource, configuration).parse();
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

		Tree tree = TreeBuilder.build(configuration, controllers);
		CiconiaHandler.initialize(tree, componentsDatabase, configuration);
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
		sb.append("└─────┘");
		return sb.toString();
	}

	private void requireNoDuplicateMappings(List<Controller> controllers){
		Map<String, ArrayList<Pair<RequestMethod, String>>> map = new HashMap<>();
		for(Controller controller : controllers){
			for(ExecutableMethod method : controller.getExecutableMethods()){
				String methodName = controller.getComponentClass().getName() + "#" + method.getMethod().getName();
				String originalMapping = controller.getMappingName() + configuration.getPathSeparator() + method.getMappingWrapper().getMappingName();
				String mapping = originalMapping.replaceAll("\\{\\w+}", ValueConstants.DEFAULT_NONE);

				ArrayList<Pair<RequestMethod, String>> mapRequestMethods = map.getOrDefault(mapping, new ArrayList<>());
				if(mapRequestMethods.isEmpty())
					map.put(mapping, mapRequestMethods);

				for(RequestMethod requestMethod : method.getMappingWrapper().getMapping().method()){
					Optional<Pair<RequestMethod, String>> op = mapRequestMethods
							.stream()
							.filter(p -> p.getFirst().equals(requestMethod))
							.findAny();
					if(op.isPresent()){
						throw new DuplicateMappingException(String.format(
								"Duplicate mapping found.\n" +
										"Mapping \"%s\" (%s) defined for\n" +
										"%s\n" +
										"and\n" +
										"%s",
								method.getMappingWrapper().getMappingName(),
								requestMethod,
								methodName,
								op.get().getSecond()
						));
					}else{
						mapRequestMethods.add(new Pair<>(requestMethod, methodName));
					}
				}
			}
		}
	}

	public static void run(Class<?> source){
		run(source, new CiconiaConfiguration.Builder().build());
	}

	public static void run(Class<?> source, CiconiaConfiguration configuration){
		new CiconiaApplication(source, configuration).run();
	}

}
