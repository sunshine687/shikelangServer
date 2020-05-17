package com.sunshine687.shikelang.util;

import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 任务管理类
 *
 * @author ZhuPengWei
 * @date 2018/5/17 10:57
 */
@Component
public class QuartzManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzManager.class);

    //创建调度容器
    Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
    /**
     * 任务组名
     */
    private static String JOB_GROUP_NAME = "SHIKELANG_JOBGROUP_NAME";
    /**
     * 触发器组名
     */
    private static String TRIGGER_GROUP_NAME = "SHIKELANG_TRIGGERGROUP_NAME";

    public QuartzManager() throws SchedulerException {
    }

    /**
     * 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
     *
     * @param jobName 任务名
     * @param cls     任务
     * @param time    任务触发时间( corn 表达式)
     */
    public void addJob(String jobName, Class cls, String time, Object params) {
        try {
            JobDetail jobDetail = new JobDetailImpl(jobName, JOB_GROUP_NAME, cls);// 任务名，任务组，任务执行类
            jobDetail.getJobDataMap().put("params", params);
            // 触发器
            CronTriggerImpl trigger = new CronTriggerImpl(jobName, TRIGGER_GROUP_NAME);// 触发器名,触发器组
            //trigger.wait();
            trigger.setCronExpression(time);// 触发器时间设定
            sched.scheduleJob(jobDetail, trigger);
            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 修改一个任务的触发时间(使用默认的任务组名 ， 触发器名 ， 触发器组名)
     *
     * @param jobName 任务名称
     * @param time    任务触发时间( corn 表达式)
     */
    public void modifyJobTime(String jobName, String time) {
        try {
            CronTriggerImpl trigger = (CronTriggerImpl) sched.getTrigger(new TriggerKey(jobName, TRIGGER_GROUP_NAME));
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(time)) {
                JobDetail jobDetail = sched.getJobDetail(new JobKey(jobName, JOB_GROUP_NAME));
                Class objJobClass = jobDetail.getJobClass();
                removeJob(jobName);
                addJob(jobName, objJobClass, time, jobDetail.getJobDataMap().get("params"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改一个任务的触发以及触发内容(使用默认的任务组名 ， 触发器名 ， 触发器组名)
     *
     * @param jobName  任务名称
     * @param jobValue 内容
     * @param time     任务触发时间( corn 表达式)
     */
    public void modifyJobTime(String jobName, String jobValue, String time) {
        try {
            CronTriggerImpl trigger = (CronTriggerImpl) sched.getTrigger(new TriggerKey(jobName, TRIGGER_GROUP_NAME));
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(time)) {
                JobDetail jobDetail = sched.getJobDetail(new JobKey(jobName, JOB_GROUP_NAME));
                Class objJobClass = jobDetail.getJobClass();
                removeJob(jobName);
                addJob(jobName, objJobClass, time, jobValue);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除一个任务(使用默认的任务组名 ， 触发器名 ， 触发器组名)
     *
     * @param jobName 任务名称
     */
    public void removeJob(String jobName) {
        try {
            sched.pauseTrigger(new TriggerKey(jobName, TRIGGER_GROUP_NAME));// 停止触发器
            sched.unscheduleJob(new TriggerKey(jobName, TRIGGER_GROUP_NAME));// 移除触发器
            sched.deleteJob(new JobKey(jobName, JOB_GROUP_NAME));// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除多个任务(使用默认的任务组名 ， 触发器名 ， 触发器组名)
     *
     * @param jobNames 任务名称集合
     */
    public void removeJobs(List<String> jobNames) {
        for (String jobName : jobNames) {
            removeJob(jobName);
            LOGGER.error("移除工作任务:{}", jobName);
        }
    }


    /**
     * 启动所有定时任务
     */
    public void startJobs() {
        try {
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭所有定时任务
     */
    public void shutdownJobs() {
        try {
            if (!sched.isShutdown()) {
                sched.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}