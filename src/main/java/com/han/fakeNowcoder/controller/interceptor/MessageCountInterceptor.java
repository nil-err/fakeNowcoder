package com.han.fakeNowcoder.controller.interceptor;

import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.service.MessageService;
import com.han.fakeNowcoder.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageCountInterceptor implements HandlerInterceptor {

  @Autowired private HostHolder hostHolder;

  @Autowired private MessageService messageService;

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {
    User user = hostHolder.getUser();

    if (user != null && modelAndView != null) {
      int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(), null);
      int unreadMessagesCount = messageService.findUnreadMessagesCount(user.getId(), null);
      modelAndView.addObject("allUnderCount", unreadNoticeCount + unreadMessagesCount);
    }
  }
}
