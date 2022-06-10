package com.maxsavteam.ciconia.graph;

import com.maxsavteam.ciconia.components.Component;
import com.maxsavteam.ciconia.exceptions.InstantiationException;
import com.maxsavteam.graph.Graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentsDependenciesGraph {

	private final Graph<Component> graph = new Graph<>();

	private final Map<Class<?>, Component> classComponentMap = new HashMap<>();

	public ComponentsDependenciesGraph(List<Component> components){
		for(Component component : components){
			classComponentMap.put(component.getComponentClass(), component);
		}
		buildGraph(components);
	}

	private void buildGraph(List<Component> components){
		graph.addVertices(components);
		for(Component component : components){
			for(Class<?> cl : component.getDependenciesClasses()){
				Component dependencyComponent = classComponentMap.get(cl);
				if(dependencyComponent == null)
					throw new InstantiationException(); // this shouldn't happen
				graph.addEdge(component, dependencyComponent);
			}
		}
	}

	public List<Component> findDependencyCycle(){
		return graph.findCycle();
	}

	public List<Component> getInTopologicalOrder(){
		return graph.getInTopologicalOrder();
	}

}
