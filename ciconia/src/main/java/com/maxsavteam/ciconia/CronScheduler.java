package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.component.Component;
import com.maxsavteam.ciconia.component.ObjectsDatabase;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class CronScheduler {

	private final Scheduler quartzScheduler;
	private final ObjectsDatabase objectsDatabase;

	public CronScheduler(ObjectsDatabase objectsDatabase) throws SchedulerException {
		quartzScheduler = StdSchedulerFactory.getDefaultScheduler();
		quartzScheduler.start();
		this.objectsDatabase = objectsDatabase;
	}

	public void shutdown() throws SchedulerException {
		quartzScheduler.shutdown();
	}

	public void scheduleCronMethod(Component component, Component.CronMethod cronMethod) throws SchedulerException {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(CronJob.OBJECTS_DATABASE_KEY, objectsDatabase);
		jobDataMap.put(CronJob.COMPONENT_KEY, component);
		jobDataMap.put(CronJob.CRON_METHOD_KEY, cronMethod);
		JobDetail jobDetail = JobBuilder.newJob(CronJob.class)
				.withIdentity(UUID.randomUUID().toString())
				.usingJobData(jobDataMap)
				.build();
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(UUID.randomUUID().toString())
				.withSchedule(CronScheduleBuilder.cronSchedule(cronMethod.getCron().value()))
				.build();
		quartzScheduler.scheduleJob(jobDetail, trigger);
	}

	public static class CronJob implements Job {

		public static final String OBJECTS_DATABASE_KEY = "objectsDatabase";
		public static final String COMPONENT_KEY = "component";
		public static final String CRON_METHOD_KEY = "cronMethod";

		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			JobDataMap map = context.getJobDetail().getJobDataMap();
			ObjectsDatabase objectsDatabase = (ObjectsDatabase) map.get(OBJECTS_DATABASE_KEY);
			Component component = (Component) map.get(COMPONENT_KEY);
			Component.CronMethod cronMethod = (Component.CronMethod) map.get(CRON_METHOD_KEY);

			Method method = cronMethod.getMethod();
			Object[] args = new Object[method.getParameterCount()];
			Class<?>[] parameterTypes = method.getParameterTypes();
			for (int i = 0; i < args.length; i++) {
				args[i] = objectsDatabase.findObject(parameterTypes[i]).orElse(null);
			}

			try {
				method.invoke(component.getClassInstance(), args);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new JobExecutionException(e);
			}
		}
	}

}
