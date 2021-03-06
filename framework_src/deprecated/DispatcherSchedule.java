package org.smarty.core.support.schedule;

import java.util.Arrays;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.smarty.core.config.SystemConfigurer;
import org.smarty.core.utils.MD5Util;
import org.smarty.core.utils.ObjectUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.quartz.SchedulerAccessor;

/**
 * @author qul
 * @since LVGG1.1
 */
public class DispatcherSchedule extends SchedulerAccessor implements BeanNameAware, BeanFactoryAware, InitializingBean {
	private final Log logger = LogFactory.getLog(DispatcherSchedule.class);
	private String beanName;
	private long startDelay;
	private Class<? extends Job> jobClass;
	private String description;
	private Scheduler scheduler;

	public void setStartDelay(long startDelay) {
		this.startDelay = startDelay;
	}

	public void setJobClass(Class<? extends Job> jobClass) {
		this.jobClass = jobClass;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void startJob(JobDataMap dataMap) {
		JobDataMap jdm = dataMap != null ? dataMap : new JobDataMap();
		JobKey key = JobKey.jobKey(parseName(jdm), beanName);
		// trigger
		SimpleTriggerImpl sti = getTrigger(key);
		if (sti == null) {
			return;
		}
		// jobDataMap
		jdm.put("jobDetail", getJobDetail(key));
		sti.setJobDataMap(jdm);
		// 注册
		setTriggers(sti);
		try {
			registerListeners();
			registerJobsAndTriggers();
		} catch (SchedulerException e) {
			logger.error(e);
		}
	}

	public void stopJob(JobDataMap dataMap) {
		JobKey key = JobKey.jobKey(parseName(dataMap), beanName);
		try {
			scheduler.deleteJob(key);
		} catch (SchedulerException e) {
			logger.error(e);
		}
	}

	@Override
	public final void afterPropertiesSet() throws Exception {

	}

	private String parseName(JobDataMap dataMap) {
		StringBuilder nb = new StringBuilder(beanName + "?");
		if (ObjectUtil.isEmpty(dataMap)) {
			nb.append("default");
			return MD5Util.encode(nb.toString());
		}
		String[] keys = dataMap.getKeys();
		Arrays.sort(keys);
		for (int i = 0, len = keys.length; i < len; i++) {
			String key = keys[i];
			nb.append(key).append("=");
			nb.append(dataMap.get(key));
			if (i < len - 1) {
				nb.append("&");
			}
		}
		return MD5Util.encode(nb.toString());
	}

	private JobDetailImpl getJobDetail(JobKey jobKey) {
		if (jobKey == null) {
			return null;
		}
		JobDetailImpl jdi = new JobDetailImpl();
		jdi.setKey(jobKey);
		jdi.setJobClass(jobClass);
		jdi.setDurability(true);
		return jdi;
	}

	private SimpleTriggerImpl getTrigger(JobKey jobKey) {
		if (jobKey == null) {
			return null;
		}
		SimpleTriggerImpl trigger = new SimpleTriggerImpl();
		long st = System.currentTimeMillis();
		if (startDelay > 0) {
			st += startDelay;
		}
		trigger.setName(jobKey.getName());
		trigger.setGroup(jobKey.getGroup());
		trigger.setJobKey(jobKey);
		trigger.setStartTime(new Date(st));
		trigger.setRepeatInterval(1);
		trigger.setRepeatCount(0);
		trigger.setPriority(0);
		trigger.setMisfireInstruction(0);
		trigger.setDescription(description);
		return trigger;
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		scheduler = beanFactory.getBean(SystemConfigurer.SCHEDULER_NAME, Scheduler.class);
	}

	@Override
	protected Scheduler getScheduler() {
		return scheduler;
	}
}
