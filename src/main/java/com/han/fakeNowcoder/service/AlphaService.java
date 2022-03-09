package com.han.fakeNowcoder.service;

import com.han.fakeNowcoder.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// @Scope("prototype")
public class AlphaService {

  @Autowired AlphaDao alphaDao;

  //    public AlphaService() {
  //        System.out.println("实例化AlphaService");
  //    }
  //
  //    @PostConstruct
  //    public void init() {
  //        System.out.println("初始化AlphaService");
  //    }
  //
  //    @PreDestroy
  //    public void destoy() {
  //        System.out.println("销毁AlphaService");
  //    }

  public String find() {
    return alphaDao.select();
  }
}
