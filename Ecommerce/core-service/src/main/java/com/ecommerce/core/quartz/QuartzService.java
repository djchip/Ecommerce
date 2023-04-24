package com.ecommerce.core.quartz;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class QuartzService {
    @Autowired
	private Scheduler scheduler;

	@PostConstruct
	public void startScheduler() {
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public void addJob(Class<? extends QuartzJobBean> jobClass, String jobName, String jobGroupName, int jobTime,
                       int jobTimes, Map jobData) {
		try {
			// Task name and group make up task key
			JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
			// Set job Parameters
			if (jobData != null && jobData.size() > 0) {
				jobDetail.getJobDataMap().putAll(jobData);
			}
			// Use the simpleTrigger rule
			Trigger trigger = null;
			if (jobTimes < 0) {
				trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName)
						.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(1).withIntervalInSeconds(jobTime))
						.startNow().build();
			} else {
				trigger = TriggerBuilder
						.newTrigger().withIdentity(jobName, jobGroupName).withSchedule(SimpleScheduleBuilder
								.repeatSecondlyForever(1).withIntervalInSeconds(jobTime).withRepeatCount(jobTimes))
						.startNow().build();
			}
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add a job
	 *
	 * @param jobClass     Task Implementation Class
	 * @param jobName      Task Name (Recommended Unique)
	 * @param jobGroupName Task Group Name
	 * @param jobTime      Time expressions (such as: 0/5 * * * *?)
	 * @param jobData      parameter
	 */
	public void addJob(Class<? extends QuartzJobBean> jobClass, String jobName, String jobGroupName, String jobTime,
			boolean isCronTab, Map jobData) {
		try {
			// Create a jobDetail instance, bind the Job implementation class
			// Indicates the name of the job, the name of the group it belongs to, and the
			// bound job class
			// Task name and group make up task key
			JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
			// Set job Parameters
			if (jobData != null && jobData.size() > 0) {
				jobDetail.getJobDataMap().putAll(jobData);
			}
			// Define Schedule Trigger Rules
			if (isCronTab) {
				// Using the cornTrigger rule
				// Trigger key
				Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName)
						.startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.SECOND))
						.withSchedule(CronScheduleBuilder.cronSchedule(jobTime)).build();
				// Register jobs and triggers in task scheduling
				scheduler.scheduleJob(jobDetail, trigger);
			}else {
				// Using the SimpleTrigger rule
				// Trigger key
				Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName)
						.startAt(DateBuilder.futureDate(Integer.parseInt(jobTime), DateBuilder.IntervalUnit.MINUTE))
						.forJob(jobDetail).build();
				// Register jobs and triggers in task scheduling
				scheduler.scheduleJob(jobDetail, trigger);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Modify a job's time expression
	 *
	 * @param jobName
	 * @param jobGroupName
	 * @param jobTime
	 */
	public void updateJob(String jobName, String jobGroupName, String jobTime) {
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
					.withSchedule(CronScheduleBuilder.cronSchedule(jobTime)).build();
			// Restart Trigger
			scheduler.rescheduleJob(triggerKey, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete Task One job
	 *
	 * @param jobName      Task Name
	 * @param jobGroupName Task Group Name
	 */
	public void deleteJob(String jobName, String jobGroupName) {
		try {
			scheduler.deleteJob(new JobKey(jobName, jobGroupName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pause a job
	 *
	 * @param jobName
	 * @param jobGroupName
	 */
	public void pauseJob(String jobName, String jobGroupName) {
		try {
			JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
			scheduler.pauseJob(jobKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Restore a job
	 *
	 * @param jobName
	 * @param jobGroupName
	 */
	public void resumeJob(String jobName, String jobGroupName) {
		try {
			JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
			scheduler.resumeJob(jobKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Execute a job immediately
	 *
	 * @param jobName
	 * @param jobGroupName
	 */
	public void runAJobNow(String jobName, String jobGroupName) {
		try {
			JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
			scheduler.triggerJob(jobKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a list of all scheduled tasks
	 *
	 * @return
	 */
	public List<Map<String, Object>> queryAllJob() {
		List<Map<String, Object>> jobList = null;
		try {
			GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
			Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
			jobList = new ArrayList<Map<String, Object>>();
			for (JobKey jobKey : jobKeys) {
				List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
				for (Trigger trigger : triggers) {
					Map<String, Object> map = new HashMap<>();
					map.put("jobName", jobKey.getName());
					map.put("jobGroupName", jobKey.getGroup());
					map.put("description", "trigger:" + trigger.getKey());
					Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
					map.put("jobStatus", triggerState.name());
					if (trigger instanceof CronTrigger) {
						CronTrigger cronTrigger = (CronTrigger) trigger;
						String cronExpression = cronTrigger.getCronExpression();
						map.put("jobTime", cronExpression);
					}
					jobList.add(map);
				}
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return jobList;
	}

	/**
	 * Get all running job s
	 *
	 * @return
	 */
	public List<Map<String, Object>> queryRunJob() {
		List<Map<String, Object>> jobList = null;
		try {
			List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
			jobList = new ArrayList<Map<String, Object>>(executingJobs.size());
			for (JobExecutionContext executingJob : executingJobs) {
				Map<String, Object> map = new HashMap<String, Object>();
				JobDetail jobDetail = executingJob.getJobDetail();
				JobKey jobKey = jobDetail.getKey();
				Trigger trigger = executingJob.getTrigger();
				map.put("jobName", jobKey.getName());
				map.put("jobGroupName", jobKey.getGroup());
				map.put("description", "trigger:" + trigger.getKey());
				Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
				map.put("jobStatus", triggerState.name());
				if (trigger instanceof CronTrigger) {
					CronTrigger cronTrigger = (CronTrigger) trigger;
					String cronExpression = cronTrigger.getCronExpression();
					map.put("jobTime", cronExpression);
				}
				jobList.add(map);
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return jobList;
	}
}
