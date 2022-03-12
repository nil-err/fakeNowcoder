package com.han.fakeNowcoder;

import com.han.fakeNowcoder.config.AlphaConfig;
import com.han.fakeNowcoder.dao.AlphaDao;
import com.han.fakeNowcoder.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = FakeNowcoderApplication.class)
class FakeNowcoderApplicationTests implements ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Test
  void contextLoads() {}

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Test
  void testApplicationContext() {
    System.out.println(applicationContext);

    AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
    System.out.println(alphaDao.select());

    alphaDao = applicationContext.getBean("alphaDaoHibernate", AlphaDao.class);
    System.out.println(alphaDao.select());
  }

  @Test
  void testBeanManagement() {
    AlphaService alphaService = applicationContext.getBean(AlphaService.class);
    System.out.println(alphaService);

    System.out.println(applicationContext.getBean(AlphaService.class));
  }

  @Test
  void testBeanConfig() {
    SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
    System.out.println(simpleDateFormat.format(new Date()));
  }

  @Autowired
  @Qualifier("alphaDaoHibernate")
  private AlphaDao alphaDao;

  @Autowired private AlphaService alphaService;

  @Autowired private AlphaConfig alphaConfig;

  //  @Autowired private SimpleDateFormat simpleDateFormat;

  @Test
  void testDI() {
    System.out.println(alphaDao);
    System.out.println(alphaService);
    System.out.println(alphaConfig);
    //    System.out.println(simpleDateFormat);
  }
}
