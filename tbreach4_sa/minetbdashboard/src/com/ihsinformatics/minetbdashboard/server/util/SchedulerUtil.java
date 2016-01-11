/**
 * Provides scheduling features (uses quartz scheduler)
 */
package com.ihsinformatics.minetbdashboard.server.util;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerUtil implements Job {
	private SchedulerFactory schedulerFactory;
	private Scheduler scheduler;
	private JobDetail jobDetail;
	private Trigger trigger;
	private String jobName;
	private String groupName;

	public SchedulerUtil() {
		try {
			schedulerFactory = new StdSchedulerFactory();
			scheduler = schedulerFactory.getScheduler();
			trigger = TriggerUtils.makeMinutelyTrigger();
			trigger.setStartTime(TriggerUtils.getEvenMinuteDate(new Date()));
			trigger.setJobName(jobName);
			trigger.setGroup(groupName);
			scheduler.scheduleJob(jobDetail, trigger);
			startJobSchedule();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public void startJobSchedule() {
		try {
			if (scheduler.isShutdown())
				scheduler.start();
			else
				throw new SchedulerException(scheduler.getSchedulerName()
						+ " is already running.");
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public void shutdownJobSchedule() {
		try {
			if (scheduler.isStarted())
				scheduler.shutdown();
			else
				throw new SchedulerException(scheduler.getSchedulerName()
						+ " is not running.");
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// Not implemented
	}
}
