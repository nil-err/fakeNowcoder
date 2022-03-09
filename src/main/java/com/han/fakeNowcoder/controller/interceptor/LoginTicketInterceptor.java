package com.han.fakeNowcoder.controller.interceptor;

import com.han.fakeNowcoder.entity.LoginTicket;
import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.service.UserService;
import com.han.fakeNowcoder.util.CookieUtil;
import com.han.fakeNowcoder.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

  @Autowired private UserService userService;

  @Autowired HostHolder hostHolder;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    // 从request中获取ticket
    String ticket = CookieUtil.getValue(request, "ticket");
    if (ticket != null) {
      // 查询凭证
      LoginTicket loginTicket = userService.findLoginTicket(ticket);
      // 查询凭证是否有效
      // 即凭证存在、状态为有效、没有超时
      if (loginTicket != null
          && loginTicket.getStatus() == 0
          && loginTicket.getExpired().after(new Date())) {
        // 查询用户
        User user = userService.findUserById(loginTicket.getUserId());
        // 在本次请求中持有用户
        hostHolder.setUser(user);
      }
    }
    return true;
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {
    User user = hostHolder.getUser();
    if (modelAndView != null && user != null) {
      modelAndView.addObject("loginUser", user);
    }
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    hostHolder.clear();
  }
}
