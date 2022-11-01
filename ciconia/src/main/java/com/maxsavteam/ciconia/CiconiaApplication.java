package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotation.Mapping;
import com.maxsavteam.ciconia.annotation.RequestMethod;
import com.maxsavteam.ciconia.component.Component;
import com.maxsavteam.ciconia.component.Configurer;
import com.maxsavteam.ciconia.component.Controller;
import com.maxsavteam.ciconia.component.ExecutableMethod;
import com.maxsavteam.ciconia.component.InstantiatableObject;
import com.maxsavteam.ciconia.component.ObjectsDatabase;
import com.maxsavteam.ciconia.component.PostInitializationMethod;
import com.maxsavteam.ciconia.exception.CiconiaInitializationException;
import com.maxsavteam.ciconia.exception.DuplicateMappingException;
import com.maxsavteam.ciconia.exception.ExecutionException;
import com.maxsavteam.ciconia.exception.InstantiationException;
import com.maxsavteam.ciconia.graph.ObjectsDependenciesGraph;
import com.maxsavteam.ciconia.parser.ComponentsParser;
import com.maxsavteam.ciconia.parser.ConfigurationsParser;
import com.maxsavteam.ciconia.processor.ComponentsProcessor;
import com.maxsavteam.ciconia.processor.ConfigurerProcessor;
import com.maxsavteam.ciconia.processor.ControllersProcessor;
import com.maxsavteam.ciconia.utils.CiconiaUtils;
import com.maxsavteam.ciconia.utils.Pair;
import org.quartz.SchedulerException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CiconiaApplication {

	private static CiconiaApplication instance;

	private final Class<?> primarySource;
	private final CiconiaConfiguration configuration;

	private CronScheduler cronScheduler;
	private List<Component> allComponentsList;

	private CiconiaApplication(Class<?> primarySource, CiconiaConfiguration configuration) {
		this.primarySource = primarySource;
		this.configuration = configuration;
	}

	private void run() {
		List<Component> components = getComponents(); // components + controllers
		List<Controller> controllers = getControllers(components); // controllers only

		requireNoDuplicateMappings(controllers);

		List<Configurer> configurers = getConfigurers();

		List<Component> allComponents = new ArrayList<>(components);
		for(Configurer configurer : configurers){
			if(allComponents.stream().noneMatch(c -> c.getaClass().equals(configurer.getaClass())))
				allComponents.add(configurer);
		}

		allComponentsList = allComponents;

		new ComponentsProcessor().setup(allComponents);

		List<InstantiatableObject> instantiatableObjects = allComponents
				.stream()
				.map(component -> (InstantiatableObject) component)
				.collect(Collectors.toList());

		for(Configurer configurer : configurers){
			instantiatableObjects.addAll(configurer.getMethods());
		}

		ObjectsDependenciesGraph graph = new ObjectsDependenciesGraph(instantiatableObjects);
		List<InstantiatableObject> cycle = graph.findDependencyCycle();
		if (!cycle.isEmpty()) {
			String cycleString = CiconiaUtils.getPrettyCycle(cycle);
			throw new InstantiationException("Dependency cycle found\n" + cycleString);
		}

		List<InstantiatableObject> topologicalOrder = graph.getInTopologicalOrder();

		ObjectsDatabase objectsDatabase = new ObjectsDatabase();

		instantiateObjects(topologicalOrder, objectsDatabase);

		if(configuration.isHandlerEnabled()) {
			MappingsContainer container = new MappingsContainer(controllers, configuration);
			CiconiaHandler.initialize(container, objectsDatabase, configuration);
		}

		try {
			scheduleCronMethods(allComponents, objectsDatabase);
		} catch (SchedulerException e) {
			throw new CiconiaInitializationException(e);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(this::callPreDestroyMethods));

		callPostInitializationMethods(configurers, objectsDatabase);
	}

	private void callPreDestroyMethods(){
		for(Component component : allComponentsList){
			for(Method method : component.getPreDestroyMethods()){
				try {
					method.invoke(component.getClassInstance());
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new ExecutionException(e);
				}
			}
		}
	}

	private void instantiateObjects(List<InstantiatableObject> objects, ObjectsDatabase objectsDatabase){
		ObjectsInstantiator instantiator = new ObjectsInstantiator(primarySource, objectsDatabase);
		for(InstantiatableObject object : objects){
			instantiator.instantiate(object);
		}
	}

	private void scheduleCronMethods(List<Component> components, ObjectsDatabase objectsDatabase) throws SchedulerException {
		cronScheduler = new CronScheduler(objectsDatabase);
		for(Component component : components){
			for(Component.CronMethod cronMethod : component.getCronMethods()){
				cronScheduler.scheduleCronMethod(component, cronMethod);
			}
		}
	}

	private void callPostInitializationMethods(List<Configurer> configurers, ObjectsDatabase objectsDatabase) {
		List<PostInitializationMethod> methods = new ArrayList<>();
		for(Configurer configurer : configurers){
			methods.addAll(configurer.getPostInitializationMethods());
		}

		methods.sort(Comparator.comparingInt(PostInitializationMethod::getOrder));

		for(PostInitializationMethod method : methods){
			try {
				method.invoke(objectsDatabase);
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ExecutionException(e);
			}
		}
	}

	private List<Component> getComponents(){
		List<Class<?>> componentsClasses = ComponentsParser.parse(primarySource);

		List<Component> components = new ArrayList<>();
		ControllersProcessor controllersProcessor = new ControllersProcessor(configuration);
		ComponentsProcessor componentsProcessor = new ComponentsProcessor();
		for(Class<?> cl : componentsClasses){
			Component component;
			if(cl.isAnnotationPresent(Mapping.class) && configuration.isHandlerEnabled()) {
				component = controllersProcessor.processControllerClass(cl);
			} else {
				component = componentsProcessor.process(cl);
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

	private List<Configurer> getConfigurers(){
		List<Class<?>> list = ConfigurationsParser.parse(primarySource);
		return new ConfigurerProcessor().process(list);
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

	private void stopApplication(){
		callPreDestroyMethods();
		try {
			cronScheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts Ciconia
	 *
	 * @param source class in root package (usually it is Main class), where Ciconia will scan for components, configurations, etc. Entities outside this package will not be found.
	 * */
	public static void run(Class<?> source) {
		run(source, new CiconiaConfiguration.Builder().build());
	}

	/**
	 * Starts Ciconia
	 *
	 * @param source class in root package (usually it is Main class), where Ciconia will scan for components, configurations, etc. Entities outside this package will not be found.
	 * @param configuration Ciconia configuration
	 * */
	public static void run(Class<?> source, CiconiaConfiguration configuration) {
		if(instance != null)
			throw new CiconiaInitializationException("Ciconia is already running");
		instance = new CiconiaApplication(source, configuration);
		instance.run();
	}

	/**
	 * Stops Ciconia
	 * */
	public static void stop(){
		if(instance == null)
			throw new CiconiaInitializationException("Ciconia is not running");
		instance.stopApplication();
		instance = null;
	}

}
