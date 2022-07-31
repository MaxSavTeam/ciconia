package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotation.RequestMethod;
import com.maxsavteam.ciconia.component.InstantiatableObject;
import com.maxsavteam.ciconia.component.ObjectsDatabase;
import com.maxsavteam.ciconia.exception.DuplicateMappingException;
import com.maxsavteam.ciconia.graph.ObjectsDependenciesGraph;
import com.maxsavteam.ciconia.component.Component;
import com.maxsavteam.ciconia.component.Controller;
import com.maxsavteam.ciconia.component.ExecutableMethod;
import com.maxsavteam.ciconia.exception.InstantiationException;
import com.maxsavteam.ciconia.parser.ComponentsParser;
import com.maxsavteam.ciconia.processor.ControllersProcessor;
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

	private void run() {
		List<Component> components = ComponentsParser.parseComponents(primarySource);

		List<Controller> controllers = new ArrayList<>();
		ControllersProcessor controllersProcessor = new ControllersProcessor(configuration);
		for(int i = 0; i < components.size(); i++){
			Controller controller = controllersProcessor.processControllerClass(components.get(i).getaClass());
			components.set(i, controller);
			controllers.add(controller);
		}

		requireNoDuplicateMappings(controllers);

		List<InstantiatableObject> instantiatableObjects = components
				.stream()
				.map(component -> (InstantiatableObject) component)
				.collect(Collectors.toList());

		ObjectsDependenciesGraph graph = new ObjectsDependenciesGraph(instantiatableObjects);
		List<InstantiatableObject> cycle = graph.findDependencyCycle();
		if (!cycle.isEmpty()) {
			String cycleString = getPrettyCycle(cycle);
			throw new InstantiationException("Dependency cycle found\n" + cycleString);
		}

		List<InstantiatableObject> topologicalOrder = graph.getInTopologicalOrder();

		ObjectsDatabase objectsDatabase = new ObjectsDatabase();

		InstantiationUtils.instantiateComponents(topologicalOrder, objectsDatabase);

		MappingsContainer container = new MappingsContainer(controllers, configuration);
		CiconiaHandler.initialize(container, objectsDatabase, configuration);
	}

	private String getPrettyCycle(List<InstantiatableObject> cycle) {
		StringBuilder sb = new StringBuilder();
		sb.append("┌─────┐\n");
		for (int i = 0; i < cycle.size(); i++) {
			Class<?> cl = cycle.get(i).getaClass();
			sb.append("|  ").append(cl.getSimpleName())
					.append(" (").append(cl.getName()).append(")")
					.append("\n");
			if (i != cycle.size() - 1) {
				sb.append(String.format("↑     ↓%n"));
			}
		}
		sb.append("└─────┘");
		return sb.toString();
	}

	private void requireNoDuplicateMappings(List<Controller> controllers) {
		Map<String, ArrayList<Pair<RequestMethod, String>>> map = new HashMap<>();
		for (Controller controller : controllers) {
			for (ExecutableMethod method : controller.getExecutableMethods()) {
				String methodName = controller.getaClass().getName() + "#" + method.getMethod().getName();
				String originalMapping = controller.getMappingName() + configuration.getPathSeparator() + method.getMappingWrapper().getMappingName();
				String mapping = originalMapping.replaceAll("\\{\\w+}", "*");

				ArrayList<Pair<RequestMethod, String>> mapRequestMethods = map.getOrDefault(mapping, new ArrayList<>());
				if (mapRequestMethods.isEmpty())
					map.put(mapping, mapRequestMethods);

				for (RequestMethod requestMethod : method.getMappingWrapper().getRequestMethods()) {
					Optional<Pair<RequestMethod, String>> op = mapRequestMethods
							.stream()
							.filter(p -> p.getFirst().equals(requestMethod))
							.findAny();
					if (op.isPresent()) {
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
					} else {
						mapRequestMethods.add(new Pair<>(requestMethod, methodName));
					}
				}
			}
		}
	}

	public static void run(Class<?> source) {
		run(source, new CiconiaConfiguration.Builder().build());
	}

	public static void run(Class<?> source, CiconiaConfiguration configuration) {
		new CiconiaApplication(source, configuration).run();
	}

}
