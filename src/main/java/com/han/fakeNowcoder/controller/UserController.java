package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.service.UserService;
import com.han.fakeNowcoder.util.CommunityUtil;
import com.han.fakeNowcoder.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author imhan
 */
@Controller
@RequestMapping(path = "/user")
public class UserController {

  public static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Value("${nowcoderCustom.path.domain}")
  private String domain;

  @Value("${server.servlet.context-path}")
  private String contextPath;

  @Value("${nowcoderCustom.path.upload}")
  private String upload;

  @Autowired private UserService userService;

  @Autowired private HostHolder hostHolder;

  @RequestMapping(path = "/setting", method = RequestMethod.GET)
  public String getSettingPage() {
    return "/site/setting";
  }

  @RequestMapping(path = "/upload", method = RequestMethod.POST)
  public String uploadHeader(MultipartFile headerImage, Model model) {
    if (headerImage == null) {
      model.addAttribute("error", "您还没有选择图片！");
      return "/site/setting";
    }
    // 获取文件名后缀
    String filename = headerImage.getOriginalFilename();
    String suffix = filename.substring(filename.lastIndexOf("."));
    if (StringUtils.isBlank(suffix)) {
      model.addAttribute("error", "图片格式错误！");
      return "/site/setting";
    }
    // 设置随机文件名
    filename = CommunityUtil.generateUUID() + suffix;
    // 设置文件路径
    File file = new File(upload + "/" + filename);
    try {
      headerImage.transferTo(file);
    } catch (IOException e) {
      logger.error("上传文件失败： " + e.getMessage());
      throw new RuntimeException("上传文件失败" + e);
    }
    // 更新当前用户headerUrl(Web路径)
    // http://localhost:8080/nowcoder/user/header/xxxxx.png
    // domain + contextPath + "/user/header/" + filename
    User user = hostHolder.getUser();
    String headerUrl = domain + contextPath + "/user/header/" + filename;
    userService.updateHeader(user.getId(), headerUrl);
    return "redirect:/index";
  }

  @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
  public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
    // 服务器图片路径
    File file = new File(upload + "/" + filename);
    String suffix = filename.substring(filename.lastIndexOf("."));
    response.setContentType("image/" + suffix);
    try (ServletOutputStream outputStream = response.getOutputStream();
        FileInputStream fileInputStream = new FileInputStream(file); ) {
      byte[] buffer = new byte[1024];
      int b = 0;
      while ((b = fileInputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, b);
      }
    } catch (IOException e) {
      logger.error("响应头像图片出错：" + e.getMessage());
    }
  }

  @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
  public String updatePassword(Model model, String password, String newPassword) {
    if (StringUtils.isBlank(password)) {
      model.addAttribute("passError", "密码不能为空！");
      return "/site/setting";
    }
    if (StringUtils.isBlank(newPassword)) {
      model.addAttribute("newPassError", "密码不能为空！");
      return "/site/setting";
    }
    User user = hostHolder.getUser();
    password = CommunityUtil.md5(password + user.getSalt());
    if (!password.equals(user.getPassword())) {
      model.addAttribute("passError", "密码错误！");
      return "/site/setting";
    }
    newPassword = CommunityUtil.md5(newPassword + user.getSalt());
    userService.updatePassword(user.getId(), newPassword);
    return "redirect:/logout";
  }

  @RequestMapping(path = "/profile", method = RequestMethod.GET)
  public String getProfilePage() {
    return "/site/profile";
  }
}
