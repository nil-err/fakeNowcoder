package com.han.fakeNowcoder.controller;

import com.google.code.kaptcha.Producer;
import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.service.UserService;
import com.han.fakeNowcoder.util.CommunityCostant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author imhan
 */
@Controller
public class LoginController implements CommunityCostant {

  private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

  @Value("${nowcoderCustom.path.domain}")
  private String domain;

  @Value("${server.servlet.context-path}")
  private String contextPath;

  @Autowired private UserService userService;

  @Autowired private Producer kaptchaProducer;

  @RequestMapping(path = "/login", method = RequestMethod.GET)
  public String getLoginPage() {
    return "/site/login";
  }

  @RequestMapping(path = "/login", method = RequestMethod.POST)
  public String login(
      Model model,
      String username,
      String password,
      String code,
      boolean rememberMe,
      HttpSession session,
      HttpServletResponse response) {
    String kaptcha = (String) session.getAttribute("kaptcha");
    if (StringUtils.isBlank(kaptcha)
        || StringUtils.isBlank(code)
        || !kaptcha.equalsIgnoreCase(code)) {
      model.addAttribute("codeMsg", "验证码不正确！");
      return "/site/login";
    }
    long expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
    Map<String, Object> map = userService.login(username, password, expiredSeconds);
    if (map.containsKey("ticket")) {
      Cookie cookie = new Cookie("ticket", (String) map.get("ticket"));
      cookie.setPath(contextPath);
      response.addCookie(cookie);
      return "redirect:/index";
    } else {
      model.addAttribute("usernameMsg", map.get("usernameMsg"));
      model.addAttribute("passwordMsg", map.get("passwordMsg"));
      return "/site/login";
    }
  }

  @RequestMapping(path = "/logout", method = RequestMethod.GET)
  public String logout(Model model, @CookieValue("ticket") String ticket) {
    userService.logout(ticket);
    return "redirect:/login";
  }

  @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
  public void getKaptcha(HttpServletResponse response, HttpSession session) {
    // 生成验证码
    String text = kaptchaProducer.createText();
    BufferedImage image = kaptchaProducer.createImage(text);
    // 验证码存入Session
    session.setAttribute("kaptcha", text);
    // 输出图片给浏览器
    response.setContentType("image/png");
    try (OutputStream outputStream = response.getOutputStream()) {
      ImageIO.write(image, "png", outputStream);
    } catch (IOException e) {
      logger.error("响应验证码失败" + e.getMessage());
    }
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

  /**
   * @param model Spring MVC Model
   * @param userId 路径变量
   * @param activationCode 路径变量
   * @return 成功则返回首页，否则跳转到操作结果页
   *     <p>// http://locahost:8080/nowcoder/activation/101/code
   */
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
