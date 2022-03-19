package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.entity.DiscussPost;
import com.han.fakeNowcoder.entity.Page;
import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.service.DiscussPostService;
import com.han.fakeNowcoder.service.LikeService;
import com.han.fakeNowcoder.service.UserService;
import com.han.fakeNowcoder.util.CommunityCostant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author imhan
 */
@Controller
public class HomeController implements CommunityCostant {

  public static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired private DiscussPostService discussPostService;

  @Autowired private UserService userService;

  @Autowired private LikeService likeService;

  @RequestMapping(path = "/index", method = RequestMethod.GET)
  public String getIndexPage(
      Model model, Page page, @RequestParam(name = "orderMode", defaultValue = "0") int orderMode) {
    /*
     方法调用之前，SpringMVC会自动实例化Model和Page，并自动将Page诸如Model
     因此，在thymeleaf中可以直接访问Page对象，无需手动将Page对象注入Model
     即 model.addAttribute("page", page);
    */
    page.setRows(discussPostService.findDiscussPostRows(0));
    page.setPath("/index?orderMode=" + orderMode);

    List<DiscussPost> list =
        discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);
    List<Map<String, Object>> discussPosts = new ArrayList<>();
    if (list != null) {
      for (DiscussPost discussPost : list) {
        Map<String, Object> map = new HashMap<>();
        map.put("post", discussPost);
        User user = userService.findUserById(discussPost.getUserId());
        map.put("user", user);
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
        map.put("likeCount", likeCount);
        discussPosts.add(map);
      }
    }
    model.addAttribute("discussPosts", discussPosts);
    model.addAttribute("orderMode", orderMode);
    return "/index";
  }

  @RequestMapping(path = "/error", method = RequestMethod.GET)
  public String getErrorPage() {
    return "/error/500";
  }

  @RequestMapping(path = "/denied", method = RequestMethod.GET)
  public String getDeniedPage() {
    return "/error/404";
  }
}
