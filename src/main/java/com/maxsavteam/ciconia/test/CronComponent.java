package com.maxsavteam.ciconia.test;

import com.maxsavteam.ciconia.annotation.Component;
import com.maxsavteam.ciconia.annotation.Cron;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component // comment if you do not want to test cron
public class CronComponent {

	private int cronExecutionsCount = 0;

	//@Cron(value = "0 * * * * ?", failurePolicy = Cron.FailurePolicy.RETRY_AFTER_TIMEOUT, retryTimeoutInSeconds = 5)
	public void cronTest(){
		cronExecutionsCount++;
		System.out.println("Cron test: " + new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new Date()));
		if(cronExecutionsCount % 3 == 1)
			throw new RuntimeException("Test exception");
	}

}
