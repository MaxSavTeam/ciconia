package com.maxsavteam.ciconia.sparkjava.annotation.handler;

import com.maxsavteam.ciconia.component.ObjectsDatabase;
import spark.Request;

import java.util.Optional;

class Utils {

	private Utils(){}

	public static Request getRequest(ObjectsDatabase contextualDatabase){
		Optional<Request> op = contextualDatabase.findObject(Request.class);
		if(op.isEmpty())
			throw new IllegalStateException("No request found in context");
		return op.get();
	}

}
