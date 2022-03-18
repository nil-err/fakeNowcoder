package com.han.fakeNowcoder.controller.interceptor;

import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.service.DataStatisticsService;
import com.han.fakeNowcoder.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataStatisticsInterceptor implements HandlerInterceptor {

  @Autowired private DataStatisticsService dataStatisticsService;

  @Autowired private HostHolder hostHolder;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    // 统计UV
    String ip = request.getRemoteHost();
    dataStatisticsService.recoedUV(ip);

    // 统计DAU
    User user = hostHolder.getUser();
    if (user != null) {
      dataStatisticsService.recoedDAU(user.getId());
    }

    return true;
  }
}
