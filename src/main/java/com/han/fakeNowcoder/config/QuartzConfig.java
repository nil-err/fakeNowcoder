package com.han.fakeNowcoder.config;

import com.han.fakeNowcoder.quartz.AlphaJob;
import com.han.fakeNowcoder.quartz.DiscussPostScoreRefreshJob;
import com.han.fakeNowcoder.quartz.WKImageDeleteJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
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
  public JobDetailFactoryBean alphaJobDetail() {
    JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
    factoryBean.setJobClass(AlphaJob.class);
    factoryBean.setName("AlphaJob");
    factoryBean.setGroup("AlphaJobGroup");
    factoryBean.setDurability(true);
    factoryBean.setRequestsRecovery(true);
    return factoryBean;
  }

  // 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
  //  @Bean
  public SimpleTriggerFactoryBean simpleTriggerFactoryBean(JobDetail alphaJobDetail) {
    SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
    factoryBean.setJobDetail(alphaJobDetail);
    factoryBean.setName("alphaTrigger");
    factoryBean.setGroup("alphaTriggerGroup");
    factoryBean.setRepeatInterval(3000);
    factoryBean.setJobDataAsMap(new JobDataMap());
    return factoryBean;
  }

  // 配置JobDetail
  @Bean
  public JobDetailFactoryBean discussPostScoreRefreshJobDetail() {
    JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
    factoryBean.setJobClass(DiscussPostScoreRefreshJob.class);
    factoryBean.setName("discussPostScoreRefreshJob");
    factoryBean.setGroup("communityJobGroup");
    factoryBean.setDurability(true);
    factoryBean.setRequestsRecovery(true);
    return factoryBean;
  }

  @Bean
  public SimpleTriggerFactoryBean discussPostScoreRefreshTriggerFactoryBean(
      JobDetail discussPostScoreRefreshJobDetail) {
    SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
    factoryBean.setJobDetail(discussPostScoreRefreshJobDetail);
    factoryBean.setName("discussPostScoreRefreshTrigger");
    factoryBean.setGroup("communityTriggerGroup");
    factoryBean.setRepeatInterval(1000 * 60);
    factoryBean.setJobDataAsMap(new JobDataMap());
    return factoryBean;
  }

  // 删除WK图片任务
  @Bean
  public JobDetailFactoryBean wkImageDeleteJobDetail() {
    JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
    factoryBean.setJobClass(WKImageDeleteJob.class);
    factoryBean.setName("wkImageDeleteJob");
    factoryBean.setGroup("communityJobGroup");
    factoryBean.setDurability(true);
    factoryBean.setRequestsRecovery(true);
    return factoryBean;
  }

  // 删除WK图片触发器
  @Bean
  public SimpleTriggerFactoryBean wkImageDeleteTrigger(JobDetail wkImageDeleteJobDetail) {
    SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
    factoryBean.setJobDetail(wkImageDeleteJobDetail);
    factoryBean.setName("wkImageDeleteTrigger");
    factoryBean.setGroup("communityTriggerGroup");
    factoryBean.setRepeatInterval(1000 * 60 * 4);
    factoryBean.setJobDataMap(new JobDataMap());
    return factoryBean;
  }
}
