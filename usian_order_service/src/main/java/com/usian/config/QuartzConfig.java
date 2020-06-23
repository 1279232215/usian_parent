package com.usian.config;


import com.usian.factory.MyAdaptableJobFactory;
import com.usian.quartz.OrderQuartz;
import org.quartz.SchedulerFactory;
import org.quartz.impl.DirectSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

/*
* quartz配置类
* */
@Configuration
public class QuartzConfig {
    /*
    * 创建job对象，去做什么事
    * */
    @Bean
    public JobDetailFactoryBean getJobDetailFactoryBean(){
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        //关联我们自己的job类
        jobDetailFactoryBean.setJobClass(OrderQuartz.class);
        return jobDetailFactoryBean;
    }

    /*
     * 创建Trigger对象，什么时间去做
     * */
    @Bean
    public CronTriggerFactoryBean getCronTriggerFactoryBean(JobDetailFactoryBean jobDetailFactoryBean){
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        //写cron表达式
        cronTriggerFactoryBean.setCronExpression("*/5 * * * * ?");
        //把job封装到Trigger里
        cronTriggerFactoryBean.setJobDetail(jobDetailFactoryBean.getObject());
        return cronTriggerFactoryBean;
    }

    /*
     * 调用Trigger
     * 什么时间去做什么事
     * */
    @Bean
    public SchedulerFactoryBean getSchedulerFactoryBean(CronTriggerFactoryBean cronTriggerFactoryBean,MyAdaptableJobFactory myAdaptableJobFactory){
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

        //把Trigger封装到Scheduler
        schedulerFactoryBean.setTriggers(cronTriggerFactoryBean.getObject());

        //将job类加载到spring容器中，因为job的orderService需要注入
        schedulerFactoryBean.setJobFactory(myAdaptableJobFactory);
        return schedulerFactoryBean;
    }
}
