package com.han.fakeNowcoder.service;

import com.han.fakeNowcoder.dao.UserMapper;
import com.han.fakeNowcoder.entity.LoginTicket;
import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.util.CommunityCostant;
import com.han.fakeNowcoder.util.CommunityUtil;
import com.han.fakeNowcoder.util.MailClient;
import com.han.fakeNowcoder.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author imhan
 */
@Service
public class UserService implements CommunityCostant {

  @Autowired private UserMapper userMapper;

  @Autowired private MailClient mailClient;

  @Autowired private TemplateEngine templateEngine;

  /*@Autowired private LoginTicketMapper loginTicketMapper;*/

  @Autowired private RedisTemplate redisTemplate;

  @Value("${nowcoderCustom.path.domain}")
  private String domain;

  @Value("${server.servlet.context-path}")
  private String contextPath;

  public User findUserById(int id) {
    /*return userMapper.selectById(id);*/
    User user = getUserCacheById(id);
    if (user == null) {
      user = initUserCacheById(id);
    }
    return user;
  }

  public Map<String, Object> register(User user) {
    Map<String, Object> map = new HashMap<>();

    // 空值判断
    if (user == null) {
      throw new IllegalArgumentException("参数不能为空");
    }
    if (StringUtils.isBlank((user.getUsername()))) {
      map.put("usernameMsg", "用户名不能为空！");
      return map;
    }
    if (StringUtils.isBlank((user.getPassword()))) {
      map.put("passwordMsg", "密码不能为空！");
      return map;
    }
    if (StringUtils.isBlank((user.getEmail()))) {
      map.put("emailMsg", "邮箱不能为空！");
      return map;
    }

    // 验证账户
    User u = userMapper.selectByName(user.getUsername());
    if (u != null) {
      map.put("usernameMsg", "用户名已存在！");
      return map;
    }

    // 验证邮箱
    u = userMapper.selectByEmail(user.getEmail());
    if (u != null) {
      map.put("emailMsg", "邮箱已被注册！");
      return map;
    }

    // 注册用户
    user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
    user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
    user.setType(0);
    user.setStatus(0);
    user.setActivationCode(CommunityUtil.generateUUID());
    user.setHeaderUrl(
        String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
    user.setCreatTime(new Date());
    userMapper.insertUser(user);

    // 发送激活邮件
    Context context = new Context();
    context.setVariable("email", user.getEmail());
    // http://locahost:8080/nowcoder/activation/101/code
    String url =
        domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
    context.setVariable("url", url);
    String content = templateEngine.process("/mail/activation", context);
    mailClient.sendMail(user.getEmail(), "激活账号", content);

    return map;
  }

  public int activation(int userId, String activationCode) {
    User user = userMapper.selectById(userId);
    if (user.getStatus() == 1) {
      return ACTIVATION_REPEAT;
    } else if (user.getActivationCode().equals(activationCode)) {
      userMapper.updateStatus(userId, 1);

      clearCache(userId);

      return ACTIVATION_SUCCESS;
    } else {
      return ACTIVATION_FAILURE;
    }
  }

  public Map<String, Object> login(String username, String password, long expiredSeconds) {
    Map<String, Object> map = new HashMap<>();

    // 空值处理
    if (StringUtils.isBlank(username)) {
      map.put("usernameMsg", "用户名不能为空！");
      return map;
    }
    if (StringUtils.isBlank(password)) {
      map.put("passwordMsg", "密码不能为空！");
      return map;
    }

    // 验证账户
    User user = userMapper.selectByName(username);
    if (user == null) {
      map.put("usernameMsg", "用户名有误！");
      return map;
    }
    if (user.getStatus() == 0) {
      map.put("usernameMsg", "账号未激活！");
      return map;
    }
    password = CommunityUtil.md5(password + user.getSalt());
    if (!user.getPassword().equals(password)) {
      map.put("passwordMsg", "密码错误！");
      return map;
    }

    // 生成登录凭证
    LoginTicket loginTicket = new LoginTicket();
    loginTicket.setUserId(user.getId());
    loginTicket.setTicket(CommunityUtil.generateUUID());
    loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));

    /*loginTicketMapper.insertLoginTicket(loginTicket);*/
    String loginTicketKey = RedisKeyUtil.getLoginTicketKey(loginTicket.getTicket());
    redisTemplate.opsForValue().set(loginTicketKey, loginTicket);

    map.put("ticket", loginTicket.getTicket());
    return map;
  }

  public void logout(String ticket) {
    /*loginTicketMapper.updateStatus(ticket, 1);*/

    String loginTicketKey = RedisKeyUtil.getLoginTicketKey(ticket);

    LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(loginTicketKey);
    loginTicket.setStatus(1);

    redisTemplate.opsForValue().set(ticket, loginTicket);
  }

  public LoginTicket findLoginTicket(String ticket) {
    /*return loginTicketMapper.selectByTicket(ticket);*/
    String loginTicketKey = RedisKeyUtil.getLoginTicketKey(ticket);
    return (LoginTicket) redisTemplate.opsForValue().get(loginTicketKey);
  }

  public int updateHeader(int userId, String headerUrl) {
    int i = userMapper.updateHeader(userId, headerUrl);

    clearCache(userId);

    return i;
  }

  public Map<String, Object> updatePassword(int userId, String password, String newPassword) {
    Map<String, Object> map = new HashMap<>();

    if (StringUtils.isBlank(password)) {
      map.put("passError", "密码不能为空！");
      return map;
    }
    if (StringUtils.isBlank(newPassword)) {
      map.put("newPassError", "密码不能为空！");
      return map;
    }
    User user = userMapper.selectById(userId);
    password = CommunityUtil.md5(password + user.getSalt());
    if (!password.equals(user.getPassword())) {
      map.put("passError", "密码错误！");
      return map;
    }
    newPassword = CommunityUtil.md5(newPassword + user.getSalt());
    userMapper.updatePassword(userId, newPassword);

    clearCache(userId);

    return map;
  }

  // 重置密码
  public Map<String, Object> resetPassword(String email, String password) {
    Map<String, Object> map = new HashMap<>();

    // 空值处理
    if (StringUtils.isBlank(email)) {
      map.put("emailMsg", "邮箱不能为空!");
      return map;
    }
    if (StringUtils.isBlank(password)) {
      map.put("passwordMsg", "密码不能为空!");
      return map;
    }

    // 验证邮箱
    User user = userMapper.selectByEmail(email);
    if (user == null) {
      map.put("emailMsg", "该邮箱尚未注册!");
      return map;
    }

    // 重置密码
    password = CommunityUtil.md5(password + user.getSalt());
    userMapper.updatePassword(user.getId(), password);

    clearCache(user.getId());

    map.put("user", user);
    return map;
  }

  public User findUserByName(String username) {
    return userMapper.selectByName(username);
  }

  /*重构临时缓存用户信息的功能*/
  /*1. 优先从缓存中取值*/
  private User getUserCacheById(int userId) {
    String userIdKey = RedisKeyUtil.getUserIdKey(userId);
    User user = (User) redisTemplate.opsForValue().get(userIdKey);
    return user;
  }

  /*2. 缓存中不存在时，初始化缓存数据*/
  private User initUserCacheById(int userId) {
    User user = userMapper.selectById(userId);

    String userIdKey = RedisKeyUtil.getUserIdKey(userId);
    redisTemplate.opsForValue().set(userIdKey, user, 3600, TimeUnit.SECONDS);

    return user;
  }

  /*3. 数据变更时清楚缓存数据*/
  private void clearCache(int userId) {
    String userIdKey = RedisKeyUtil.getUserIdKey(userId);
    redisTemplate.delete(userIdKey);
  }
}
