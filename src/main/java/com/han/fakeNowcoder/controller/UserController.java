package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.annotation.LoginRequired;
import com.han.fakeNowcoder.entity.Comment;
import com.han.fakeNowcoder.entity.DiscussPost;
import com.han.fakeNowcoder.entity.Page;
import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.service.*;
import com.han.fakeNowcoder.util.CommunityCostant;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author imhan
 */
@Controller
@RequestMapping(path = "/user")
public class UserController implements CommunityCostant {

  public static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Value("${nowcoderCustom.path.domain}")
  private String domain;

  @Value("${server.servlet.context-path}")
  private String contextPath;

  @Value("${nowcoderCustom.path.upload}")
  private String upload;

  @Autowired private UserService userService;

  @Autowired private HostHolder hostHolder;

  @Autowired private CommentService commentService;

  @Autowired private DiscussPostService discussPostService;

  @Autowired private LikeService likeService;

  @Autowired private FollowService followService;

  @LoginRequired
  @RequestMapping(path = "/setting", method = RequestMethod.GET)
  public String getSettingPage() {
    return "/site/setting";
  }

  @LoginRequired
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
        FileInputStream fileInputStream = new FileInputStream(file)) {
      byte[] buffer = new byte[1024];
      int b = 0;
      while ((b = fileInputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, b);
      }
    } catch (IOException e) {
      logger.error("响应头像图片出错：" + e.getMessage());
    }
  }

  @LoginRequired
  @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
  public String updatePassword(Model model, String password, String newPassword) {

    User user = hostHolder.getUser();
    Map<String, Object> map = userService.updatePassword(user.getId(), password, newPassword);
    if (map == null || map.isEmpty()) {
      return "redirect:/logout";
    } else {
      model.addAttribute("passError", map.get("passError"));
      model.addAttribute("newPassnewPassErrorError", map.get("newPassError"));
      return "/site/setting";
    }
  }

  /**
   * 个人主页
   *
   * @param userId
   * @return
   */
  @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
  public String getProfilePage(@PathVariable("userId") int userId, Model model) {
    User user = userService.findUserById(userId);
    if (user == null) {
      throw new IllegalArgumentException("该用户不存在");
    }

    // 用户
    model.addAttribute("user", user);
    // 获赞数量
    int obtainedLikeCount = likeService.findUserObtainedLikeCount(userId);
    model.addAttribute("obtainedLikeCount", obtainedLikeCount);

    // 关注数量
    long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
    model.addAttribute("followeeCount", followeeCount);

    // 粉丝数量
    long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
    model.addAttribute("followerCount", followerCount);

    // 关注状态
    boolean followStatus = false;
    if (hostHolder.getUser() != null) {
      followStatus =
          followService.findFollowStatute(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }
    model.addAttribute("followStatus", followStatus);

    return "/site/profile";
  }

  @RequestMapping(path = "/mypost/{userId}", method = RequestMethod.GET)
  public String getMyDiscussPost(@PathVariable("userId") int userId, Model model, Page page) {
    // 用户
    User user = userService.findUserById(userId);
    if (user == null) {
      throw new RuntimeException("该用户不存在！");
    }
    model.addAttribute("user", user);

    // 分页信息
    page.setPath("/user/mypost/" + userId);
    page.setLimit(5);
    page.setRows(discussPostService.findDiscussPostRows(userId));

    // 帖子列表
    List<DiscussPost> discussPosts =
        discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit());
    List<Map<String, Object>> discussPostList = new ArrayList<>();
    if (discussPosts != null) {
      for (DiscussPost discussPost : discussPosts) {
        Map<String, Object> map = new HashMap<>();
        map.put("discussPost", discussPost);
        map.put(
            "likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId()));
        discussPostList.add(map);
      }
    }
    model.addAttribute("discussPostList", discussPostList);

    return "/site/my-post";
  }

  @RequestMapping(path = "/myreply/{userId}", method = RequestMethod.GET)
  public String getMyReplyPost(@PathVariable("userId") int userId, Model model, Page page) {
    // 用户
    User user = userService.findUserById(userId);
    if (user == null) {
      throw new RuntimeException("该用户不存在！");
    }
    model.addAttribute("user", user);

    // 分页信息
    page.setPath("/user/myreply/" + userId);
    page.setLimit(5);
    page.setRows(commentService.findCommentRowsByUser(userId));

    // 帖子列表
    List<Comment> comments =
        commentService.findCommentByUser(userId, page.getOffset(), page.getLimit());
    List<Map<String, Object>> commentList = new ArrayList<>();
    if (comments != null) {
      for (Comment comment : comments) {
        Map<String, Object> map = new HashMap<>();
        map.put("comment", comment);
        map.put("discussPost", discussPostService.findDiscussPostById(comment.getEntityId()));
        commentList.add(map);
      }
    }
    model.addAttribute("commentList", commentList);
    return "/site/my-reply";
  }
}
