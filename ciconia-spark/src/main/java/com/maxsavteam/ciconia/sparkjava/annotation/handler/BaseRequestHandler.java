package com.maxsavteam.ciconia.sparkjava.annotation.handler;

import com.maxsavteam.ciconia.component.ObjectsDatabase;
import spark.Request;

import java.util.Optional;

public class BaseRequestHandler {

	protected Request getRequest(ObjectsDatabase contextualDatabase){
		Optional<Request> op = contextualDatabase.findSuitableObject(Request.class);
		if(op.isEmpty())
			throw new IllegalStateException("No request found in context");
		return op.get();
	}

}
