package com.maxsavteam.ciconia.sparkjava;

import com.maxsavteam.ciconia.CiconiaApplication;
import com.maxsavteam.ciconia.CiconiaHandler;
import com.maxsavteam.ciconia.annotation.RequestMethod;
import com.maxsavteam.ciconia.component.ObjectsDatabase;
import com.maxsavteam.ciconia.exception.CiconiaRuntimeException;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Spark;

public class CiconiaSparkApplication {

	private final CiconiaSparkConfiguration configuration;

	public CiconiaSparkApplication(CiconiaSparkConfiguration configuration) {
		this.configuration = configuration;
	}

	private void run(Class<?> clazz) {
		CiconiaApplication.run(clazz, configuration);

		Spark.port(configuration.getPort());

		Spark.get("*", (request, response) -> handleRequest(request, response, RequestMethod.GET));
		Spark.post("*", (request, response) -> handleRequest(request, response, RequestMethod.POST));
		Spark.put("*", (request, response) -> handleRequest(request, response, RequestMethod.PUT));
		Spark.delete("*", (request, response) -> handleRequest(request, response, RequestMethod.DELETE));
		Spark.patch("*", (request, response) -> handleRequest(request, response, RequestMethod.PATCH));
		Spark.head("*", (request, response) -> handleRequest(request, response, RequestMethod.HEAD));

		if (configuration.getExceptionHandler() != null)
			Spark.exception(CiconiaRuntimeException.class, configuration.getExceptionHandler());
	}

	public static void run(Class<?> clazz, CiconiaSparkConfiguration configuration) {
		new CiconiaSparkApplication(configuration).run(clazz);
	}

	private Object handleRequest(Request request, Response response, RequestMethod requestMethod) {
		String path = request.pathInfo();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("method", path);
		JSONObject params = new JSONObject();
		for (String attribute : request.queryParams())
			params.put(attribute, request.queryParams(attribute));
		jsonObject.put("params", params);

		ObjectsDatabase database = new ObjectsDatabase();
		database.addObject(request, Request.class);
		database.addObject(response, Response.class);

		Object result = CiconiaHandler.getInstance().handle(
				jsonObject,
				requestMethod,
				database
		);
		if(result == null)
			return null;
		if(result == CiconiaHandler.ASYNC_METHOD || result == CiconiaHandler.VOID)
			return response.body();
		return result;
	}

}
