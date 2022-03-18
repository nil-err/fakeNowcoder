package com.han.fakeNowcoder.config;

import com.han.fakeNowcoder.quartz.AlphaJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

// 配置 -> 仅仅是第一次读取到，初始化到数据库里 -> 调用是访问数据库，之后不访问配置
@Configuration
public class QuartzConfig {
  /* FactoryBean 可简化Bean的实例化过程
   * 1. Spring通过FactoryBean封装了某些Bean的实例化过程
   * 2. 将FactoryBean装配到Spring容器
   * 3. 将FactoryBean注入给其他的Bean
   * 4. 那么该Bean得到的是FactoryBean所管理的对象实例*/

  // 配置JobDetail
  //  @Bean
  public JobDetailFactoryBean aplhaJobDetail() {
    JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
    factoryBean.setJobClass(AlphaJob.class);
    factoryBean.setName("AlphaJob");
    factoryBean.setGroup("AlphaJobGroup");
    factoryBean.setDurability(true);
    factoryBean.setRequestsRecovery(true);
    return factoryBean;
  }

  // 篇日志Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
  //  @Bean
  public SimpleTriggerFactoryBean simpleTriggerFactoryBean(JobDetail aplhaJobDetail) {
    SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
    factoryBean.setJobDetail(aplhaJobDetail);
    factoryBean.setName("alphaTrigger");
    factoryBean.setGroup("alphaTriggerGroup");
    factoryBean.setRepeatInterval(3000);
    factoryBean.setJobDataAsMap(new JobDataMap());
    return factoryBean;
  }
}
