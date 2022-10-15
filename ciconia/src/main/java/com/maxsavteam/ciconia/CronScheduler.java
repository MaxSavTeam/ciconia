package com.maxsavteam.ciconia;

import com.maxsavteam.ciconia.annotation.Cron;
import com.maxsavteam.ciconia.component.Component;
import com.maxsavteam.ciconia.component.ObjectsDatabase;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

public class CronScheduler {

	private final Scheduler quartzScheduler;
	private final ObjectsDatabase objectsDatabase;

	public CronScheduler(ObjectsDatabase objectsDatabase) throws SchedulerException {
		quartzScheduler = StdSchedulerFactory.getDefaultScheduler();
		quartzScheduler.getListenerManager().addJobListener(getJobListener());
		quartzScheduler.start();
		this.objectsDatabase = objectsDatabase;
	}

	private JobListener getJobListener(){
		return new JobListener() {
			@Override
			public String getName() {
				return "main";
			}

			@Override
			public void jobToBeExecuted(JobExecutionContext context) {

			}

			@Override
			public void jobExecutionVetoed(JobExecutionContext context) {

			}

			@Override
			public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
				if(jobException == null)
					return;

				Component.CronMethod cronMethod = (Component.CronMethod) context.getJobDetail().getJobDataMap().get(CronJob.CRON_METHOD_KEY);

				if(cronMethod.getCron().failurePolicy() != Cron.FailurePolicy.RETRY_AFTER_TIMEOUT)
					return;

				Scheduler scheduler = context.getScheduler();
				Trigger newTrigger = getTriggerBuilder(cronMethod)
						.startAt(new Date(System.currentTimeMillis() + cronMethod.getCron().retryTimeoutInSeconds() * 1000))
						.build();
				try {
					scheduler.rescheduleJob(context.getTrigger().getKey(), newTrigger);
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			}
		};
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
		Trigger trigger = getTriggerBuilder(cronMethod).build();
		quartzScheduler.scheduleJob(jobDetail, trigger);
	}

	private TriggerBuilder<CronTrigger> getTriggerBuilder(Component.CronMethod cronMethod){
		return TriggerBuilder.newTrigger()
				.withIdentity(UUID.randomUUID().toString())
				.withSchedule(CronScheduleBuilder.cronSchedule(cronMethod.getCron().value()));
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

			boolean isRepeatImmediately = cronMethod.getCron().failurePolicy() == Cron.FailurePolicy.RETRY_IMMEDIATELY;

			Method method = cronMethod.getMethod();
			Object[] args = new Object[method.getParameterCount()];
			Class<?>[] parameterTypes = method.getParameterTypes();
			for (int i = 0; i < args.length; i++) {
				args[i] = objectsDatabase.findObject(parameterTypes[i]).orElse(null);
			}

			try {
				method.invoke(component.getClassInstance(), args);
			} catch (IllegalAccessException | InvocationTargetException e) {
				JobExecutionException jobExecutionException = new JobExecutionException(e);
				jobExecutionException.setRefireImmediately(isRepeatImmediately);
				throw jobExecutionException;
			}
		}
	}

}
