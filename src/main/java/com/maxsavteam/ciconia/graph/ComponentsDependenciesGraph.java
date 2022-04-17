package com.maxsavteam.ciconia.graph;

import com.maxsavteam.ciconia.components.Component;
import com.maxsavteam.ciconia.exceptions.InstantiationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentsDependenciesGraph {

	private final List<Component> components;

	private final Map<Class<?>, Component> classComponentMap = new HashMap<>();

	private final Map<Component, ArrayList<Component>> graphMap = new HashMap<>();
	private final Map<Component, ArrayList<Component>> reversedGraphMap = new HashMap<>();

	private final ArrayList<Component> roots = new ArrayList<>();

	private Component cycleStart;
	private Component cycleEnd;

	public ComponentsDependenciesGraph(List<Component> components){
		this.components = components;

		for(Component component : components){
			classComponentMap.put(component.getComponentClass(), component);
		}
		buildGraph(components);
		findRoots();
	}

	private void buildGraph(List<Component> components){
		for(Component component : components){
			ArrayList<Component> dependencies = new ArrayList<>();
			for(Class<?> cl : component.getDependenciesClasses()){
				Component dependencyComponent = classComponentMap.get(cl);
				if(dependencyComponent == null)
					throw new InstantiationException(); // this shouldn't be happening
				dependencies.add(dependencyComponent);
			}
			graphMap.put(component, dependencies);

			for(Component dependencyComponent : dependencies){
				if(reversedGraphMap.containsKey(dependencyComponent))
					reversedGraphMap.get(dependencyComponent).add(component);
				else
					reversedGraphMap.put(dependencyComponent, new ArrayList<>(List.of(component)));
			}
		}
	}

	private void findRoots(){
		for(Component component : graphMap.keySet()){
			if(!reversedGraphMap.containsKey(component) || reversedGraphMap.get(component).isEmpty())
				roots.add(component);
		}
	}

	public List<Component> findDependencyCycle(){
		Map<Component, Integer> componentColorMap = new HashMap<>();
		Map<Component, Component> ancestorsMap = new HashMap<>();
		ArrayList<Component> cycle = new ArrayList<>();
		for(Component start : components){
			if(findCycle(start, componentColorMap, ancestorsMap)){
				cycle.add(cycleStart);
				for(Component component = cycleEnd; component != cycleStart; component = ancestorsMap.get(component)){
					cycle.add(component);
				}
				return cycle;
			}
		}
		return new ArrayList<>();
	}

	private boolean findCycle(Component current, Map<Component, Integer> componentColorMap, Map<Component, Component> ancestorMap){
		componentColorMap.put(current, 1);
		for(Component next : graphMap.getOrDefault(current, new ArrayList<>())){
			int color = componentColorMap.getOrDefault(next, 0);
			if(color == 0){
				ancestorMap.put(next, current);
				if(findCycle(next, componentColorMap, ancestorMap))
					return true;
			}else if(color == 1){
				cycleStart = next;
				cycleEnd = current;
				return true;
			}
		}
		componentColorMap.put(current, 2);
		return false;
	}

	public List<Component> getInTopologicalOrder(){
		ArrayList<Component> list = new ArrayList<>();
		Map<Component, Boolean> usedMap = new HashMap<>();
		for(Component root : roots){
			dfs(root, usedMap, list);
		}
		return list;
	}

	private void dfs(Component current, Map<Component, Boolean> usedMap, ArrayList<Component> list){
		usedMap.put(current, true);
		for(Component next : graphMap.getOrDefault(current, new ArrayList<>())){
			if(Boolean.FALSE.equals(usedMap.getOrDefault(next, false)))
				dfs(next, usedMap, list);
		}
		list.add(current);
	}

}
