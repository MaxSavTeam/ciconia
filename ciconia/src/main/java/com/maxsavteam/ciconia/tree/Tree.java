package com.maxsavteam.ciconia.tree;

import com.maxsavteam.ciconia.CiconiaConfiguration;
import com.maxsavteam.ciconia.annotation.RequestMethod;
import com.maxsavteam.ciconia.component.Controller;
import com.maxsavteam.ciconia.component.ExecutableMethod;

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
		List<String> parts = splitMapping(controller.getMappingName());
		addController(controller, parts, 0, headNode);
	}

	private List<String> splitMapping(String mapping){
		String[] parts = mapping.split("\\Q" + configuration.getPathSeparator() + "\\E");
		List<String> list = new ArrayList<>(List.of(parts));
		if(mapping.endsWith(String.valueOf(configuration.getPathSeparator()))){
			list.add("");
		}
		return list;
	}

	private void addController(Controller controller, List<String> mappingParts, int partPosition, Node node){
		if(partPosition >= mappingParts.size()){
			node.addController(controller);
			return;
		}
		String part = mappingParts.get(partPosition);
		Node nextNode = node.getNodeMap().get(part);
		if(nextNode == null){
			nextNode = new Node();
			node.addNode(part, nextNode);
		}
		addController(controller, mappingParts, partPosition + 1, nextNode);
	}

	public Optional<MethodSearchResult> findMethod(String methodName, RequestMethod requestMethod){
		List<String> parts = splitMapping(methodName);
		return findMethod(headNode, parts, 0, requestMethod);
	}

	public Optional<MethodSearchResult> findMethod(Node node, List<String> mappingParts, int partPosition, RequestMethod requestMethod){
		if(partPosition >= mappingParts.size())
			return Optional.empty();

		String methodMapping = join(mappingParts, partPosition);
		for(Controller controller : node.getControllers()){
			Optional<ExecutableMethod> op = controller.findMethodByMapping(methodMapping, requestMethod);
			if(op.isPresent()){
				ExecutableMethod method = op.get();
				Map<String, String> pathVariablesMap = method.getMappingWrapper().extractPathVariables(methodMapping);
				MethodSearchResult result = new MethodSearchResult(controller, method, pathVariablesMap);
				return Optional.of(result);
			}
		}
		String part = mappingParts.get(partPosition);
		Node nextNode = node.getNodeMap().get(part);
		if(nextNode == null)
			return Optional.empty();
		return findMethod(nextNode, mappingParts, partPosition + 1, requestMethod);
	}

	private String join(List<String> parts, int offset){
		StringBuilder sb = new StringBuilder();
		for(int i = offset; i < parts.size(); i++){
			sb.append(parts.get(i));
			if(i != parts.size() - 1)
				sb.append(configuration.getPathSeparator());
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

	public static class MethodSearchResult {
		private final Controller controller;
		private final ExecutableMethod method;
		private final Map<String, String> pathVariablesMap;

		public MethodSearchResult(Controller controller, ExecutableMethod method, Map<String, String> pathVariablesMap) {
			this.controller = controller;
			this.method = method;
			this.pathVariablesMap = pathVariablesMap;
		}

		public Controller getController() {
			return controller;
		}

		public ExecutableMethod getMethod() {
			return method;
		}

		public Map<String, String> getPathVariablesMap() {
			return pathVariablesMap;
		}
	}

}
