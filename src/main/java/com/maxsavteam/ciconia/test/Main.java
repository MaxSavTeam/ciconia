package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.CiconiaApplication;
import com.maxsavteam.ciconia.CiconiaHandler;
import com.maxsavteam.ciconia.annotations.Component;
import com.maxsavteam.ciconia.annotations.Mapping;
import com.maxsavteam.ciconia.annotations.Param;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@Component
@Mapping("test")
public class Main {

	public static void main(String[] args) {
		CiconiaApplication.run(Main.class);
		Object result = CiconiaHandler.getInstance().handle(
				new JSONObject()
						.put("method", "test.test")
						.put("params", new JSONObject()
								.put("list", new JSONArray().put("hello").put("world"))
								.put("x", "Oleg")
						)
		);
		System.out.println(result);
	}

	@Mapping("test")
	public void helloWorld(@Param("list") List<String> list){
		for(var i : list)
			System.out.println(i);
	}

	@Mapping("test.component")
	public String testComponent(TestComponent testComponent, @Param("x") String x){
		return testComponent.test(x);
	}

}
