package com.maxsavteam.ciconia.tree;

import com.maxsavteam.ciconia.CiconiaConfiguration;
import com.maxsavteam.ciconia.annotations.RequestMethod;
import com.maxsavteam.ciconia.components.Controller;
import com.maxsavteam.ciconia.components.ExecutableMethod;
import com.maxsavteam.ciconia.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Tree {

	private final Node headNode = new Node();

	private final CiconiaConfiguration configuration;

	public Tree(CiconiaConfiguration configuration) {
		this.configuration = configuration;
	}

	public void addController(Controller controller){
		String[] parts = splitMapping(controller.getMappingName());
		addController(controller, parts, 0, headNode);
	}

	private String[] splitMapping(String mapping){
		return mapping.split("\\Q" + configuration.getPathSeparator() + "\\E");
	}

	private void addController(Controller controller, String[] mappingParts, int partPosition, Node node){
		if(partPosition >= mappingParts.length){
			node.addController(controller);
			return;
		}
		String part = mappingParts[partPosition];
		Node nextNode = node.getNodeMap().get(part);
		if(nextNode == null){
			nextNode = new Node();
			node.addNode(part, nextNode);
		}
		addController(controller, mappingParts, partPosition + 1, nextNode);
	}

	public Optional<Pair<Controller, ExecutableMethod>> findMethod(String methodName, RequestMethod requestMethod){
		String[] parts = splitMapping(methodName);
		return findMethod(headNode, parts, 0, requestMethod);
	}

	public Optional<Pair<Controller, ExecutableMethod>> findMethod(Node node, String[] mappingParts, int partPosition, RequestMethod requestMethod){
		if(partPosition >= mappingParts.length)
			return Optional.empty();

		String methodMapping = join(mappingParts, partPosition);
		for(Controller controller : node.getControllers()){
			Optional<ExecutableMethod> op = controller.findMethodByMapping(methodMapping, requestMethod);
			if(op.isPresent()){
				return Optional.of(new Pair<>(controller, op.get()));
			}
		}
		String part = mappingParts[partPosition];
		Node nextNode = node.getNodeMap().get(part);
		if(nextNode == null)
			return Optional.empty();
		return findMethod(nextNode, mappingParts, partPosition + 1, requestMethod);
	}

	private String join(String[] parts, int offset){
		StringBuilder sb = new StringBuilder();
		for(int i = offset; i < parts.length; i++){
			sb.append(parts[i]);
			if(i != parts.length - 1)
				sb.append(".");
		}
		return sb.toString();
	}

	public static class Node{

		private final Map<String, Node> nodeMap = new HashMap<>();
		private final ArrayList<Controller> controllers = new ArrayList<>();

		public Map<String, Node> getNodeMap() {
			return nodeMap;
		}

		public void addNode(String key, Node node){
			if(nodeMap.containsKey(key))
				throw new IllegalArgumentException("Node with such key already exists");
			nodeMap.put(key, node);
		}

		public void addController(Controller controller){
			controllers.add(controller);
		}

		public List<Controller> getControllers() {
			return controllers;
		}
	}

}
