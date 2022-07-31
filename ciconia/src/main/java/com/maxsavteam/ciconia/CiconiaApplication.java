package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotation.Mapping;
import com.maxsavteam.ciconia.annotation.RequestMethod;
import com.maxsavteam.ciconia.component.Component;
import com.maxsavteam.ciconia.component.Configurer;
import com.maxsavteam.ciconia.component.Controller;
import com.maxsavteam.ciconia.component.ExecutableMethod;
import com.maxsavteam.ciconia.component.InstantiatableObject;
import com.maxsavteam.ciconia.component.ObjectFactoryMethod;
import com.maxsavteam.ciconia.component.ObjectsDatabase;
import com.maxsavteam.ciconia.exception.DuplicateMappingException;
import com.maxsavteam.ciconia.exception.InstantiationException;
import com.maxsavteam.ciconia.graph.ObjectsDependenciesGraph;
import com.maxsavteam.ciconia.parser.ComponentsParser;
import com.maxsavteam.ciconia.parser.ConfigurationsParser;
import com.maxsavteam.ciconia.processor.ConfigurerProcessor;
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
		List<Component> components = getComponents();
		List<Controller> controllers = getControllers(components);

		requireNoDuplicateMappings(controllers);

		List<InstantiatableObject> instantiatableObjects = components
				.stream()
				.map(component -> (InstantiatableObject) component)
				.collect(Collectors.toList());

		List<Configurer> configurers = getConfigurers();
		for(Configurer configurer : configurers){
			boolean isThisClassAlreadyIncluded = false;
			for(InstantiatableObject instantiatableObject : instantiatableObjects){
				if(instantiatableObject.getaClass().equals(configurer.getaClass())){
					isThisClassAlreadyIncluded = true;
					break;
				}
			}
			if(!isThisClassAlreadyIncluded)
				instantiatableObjects.add(configurer);
			instantiatableObjects.addAll(configurer.getMethods());
		}

		ObjectsDependenciesGraph graph = new ObjectsDependenciesGraph(instantiatableObjects);
		List<InstantiatableObject> cycle = graph.findDependencyCycle();
		if (!cycle.isEmpty()) {
			String cycleString = getPrettyCycle(cycle);
			throw new InstantiationException("Dependency cycle found\n" + cycleString);
		}

		List<InstantiatableObject> topologicalOrder = graph.getInTopologicalOrder();

		ObjectsDatabase objectsDatabase = new ObjectsDatabase();

		InstantiationUtils.instantiateComponents(topologicalOrder, objectsDatabase);

		if(configuration.isHandlerEnabled()) {
			MappingsContainer container = new MappingsContainer(controllers, configuration);
			CiconiaHandler.initialize(container, objectsDatabase, configuration);
		}
	}

	private List<Component> getComponents(){
		List<Class<?>> componentsClasses = ComponentsParser.parse(primarySource);

		List<Component> components = new ArrayList<>();
		ControllersProcessor controllersProcessor = new ControllersProcessor(configuration);
		for(Class<?> cl : componentsClasses){
			Component component;
			if(cl.isAnnotationPresent(Mapping.class) && configuration.isHandlerEnabled()) {
				component = controllersProcessor.processControllerClass(cl);
			} else {
				component = new Component(cl);
			}
			components.add(component);
		}
		return components;
	}

	private List<Controller> getControllers(List<Component> components){
		return components
				.stream()
				.filter(component -> component instanceof Controller)
				.map(component -> (Controller) component)
				.collect(Collectors.toList());
	}

	public List<Configurer> getConfigurers(){
		List<Class<?>> list = ConfigurationsParser.parse(primarySource);
		return new ConfigurerProcessor().process(list);
	}

	private String getPrettyCycle(List<InstantiatableObject> cycle) {
		StringBuilder sb = new StringBuilder();
		sb.append("┌─────┐\n");
		for (int i = 0; i < cycle.size(); i++) {
			String name;
			InstantiatableObject object = cycle.get(i);
			if(object instanceof ObjectFactoryMethod){
				ObjectFactoryMethod method = (ObjectFactoryMethod) object;
				name = String.format(
						"Object factory method %s in %s",
						method.getMethod().getName(),
						method.getMethod().getDeclaringClass().getName()
				);
			} else{
				Class<?> cl = object.getaClass();
				name = String.format(
						"Component %s (%s)",
						cl.getName(),
						cl.getSimpleName()
				);
			}
			sb.append("|  ").append(name)
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
