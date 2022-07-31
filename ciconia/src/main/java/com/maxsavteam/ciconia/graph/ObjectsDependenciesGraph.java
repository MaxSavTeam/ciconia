package com.maxsavteam.ciconia.graph;

import com.maxsavteam.ciconia.component.InstantiatableObject;
import com.maxsavteam.ciconia.exception.InstantiationException;
import com.maxsavteam.graph.Graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectsDependenciesGraph {

	private final Graph<InstantiatableObject> graph = new Graph<>();

	private final Map<Class<?>, InstantiatableObject> classObjectsMap = new HashMap<>();

	public ObjectsDependenciesGraph(List<InstantiatableObject> objects){
		for(InstantiatableObject object : objects){
			classObjectsMap.put(object.getaClass(), object);
		}
		buildGraph(objects);
	}

	private void buildGraph(List<InstantiatableObject> objects){
		graph.addVertices(objects);
		for(InstantiatableObject object : objects){
			for(Class<?> cl : object.getDependenciesClasses()){
				InstantiatableObject dependency = classObjectsMap.get(cl);
				if(dependency == null)
					throw new InstantiationException(); // this shouldn't happen
				graph.addEdge(object, dependency);
			}
		}
	}

	public List<InstantiatableObject> findDependencyCycle(){
		return graph.findCycle();
	}

	public List<InstantiatableObject> getInTopologicalOrder(){
		return graph.getInTopologicalOrder();
	}

}
