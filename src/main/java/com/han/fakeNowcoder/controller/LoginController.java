package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.service.UserService;
import com.han.fakeNowcoder.util.CommunityCostant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityCostant {

  @Autowired private UserService userService;

  @RequestMapping(path = "/login", method = RequestMethod.GET)
  public String getLoginPage() {
    return "/site/login";
  }

  @RequestMapping(path = "/register", method = RequestMethod.GET)
  public String getRegisterPage() {
    return "/site/register";
  }

  @RequestMapping(path = "/register", method = RequestMethod.POST)
  public String register(Model model, User user) {
    Map<String, Object> map = userService.register(user);
    if (map == null || map.isEmpty()) {
      model.addAttribute("msg", "注册成功，已经发送激活邮件，请尽快激活");
      model.addAttribute("target", "/index");
      return "/site/operate-result";
    } else {
      model.addAttribute("usernameMsg", map.get("usernameMsg"));
      model.addAttribute("passwordMsg", map.get("passwordMsg"));
      model.addAttribute("emailMsg", map.get("emailMsg"));
      return "/site/register";
    }
  }

  // http://locahost:8080/nowcoder/activation/101/code
  @RequestMapping(path = "/activation/{userId}/{activationCode}", method = RequestMethod.GET)
  public String activation(
      Model model,
      @PathVariable("userId") int userId,
      @PathVariable("activationCode") String activationCode) {
    int result = userService.activation(userId, activationCode);
    if (result == ACTIVATION_SUCCESS) {
      model.addAttribute("msg", "激活成功，账号可以正常使用！");
      model.addAttribute("target", "/login");
    } else if (result == ACTIVATION_REPEAT) {
      model.addAttribute("msg", "账号已经激活成功，无效操作！");
      model.addAttribute("target", "/index");
    } else {
      model.addAttribute("msg", "激活链接不正确");
      model.addAttribute("target", "/index");
    }
    return "/site/operate-result";
  }
}
