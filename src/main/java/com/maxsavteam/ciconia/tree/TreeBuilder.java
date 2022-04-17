package com.maxsavteam.ciconia.tree;

import com.maxsavteam.ciconia.components.Controller;

import java.util.List;

public class TreeBuilder {

	private TreeBuilder(){}

	public static Tree build(List<Controller> controllers){
		Tree tree = new Tree();
		for(Controller controller : controllers){
			tree.addController(controller);
		}
		return tree;
	}

}
