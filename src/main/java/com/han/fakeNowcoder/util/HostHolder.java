package com.han.fakeNowcoder.util;

import com.han.fakeNowcoder.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author imhan
 */
@Component
public class HostHolder {

  private final ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

  public void setUser(User user) {
    userThreadLocal.set(user);
  }

  public User getUser() {
    return userThreadLocal.get();
  }

  public void clear() {
    userThreadLocal.remove();
  }
}
