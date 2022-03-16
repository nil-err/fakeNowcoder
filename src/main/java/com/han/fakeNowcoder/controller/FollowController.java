package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.annotation.LoginRequired;
import com.han.fakeNowcoder.entity.Event;
import com.han.fakeNowcoder.entity.Page;
import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.event.EventProducer;
import com.han.fakeNowcoder.service.CommentService;
import com.han.fakeNowcoder.service.DiscussPostService;
import com.han.fakeNowcoder.service.FollowService;
import com.han.fakeNowcoder.service.UserService;
import com.han.fakeNowcoder.util.CommunityCostant;
import com.han.fakeNowcoder.util.CommunityUtil;
import com.han.fakeNowcoder.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityCostant {

  @Autowired private FollowService followService;

  @Autowired private HostHolder hostHolder;

  @Autowired private UserService userService;

  @Autowired private DiscussPostService discussPostService;

  @Autowired private CommentService commentService;

  @Autowired private EventProducer eventProducer;

  @LoginRequired
  @RequestMapping(path = "/follow", method = RequestMethod.POST)
  @ResponseBody
  public String follow(int entityType, int entityId) {
    User user = hostHolder.getUser();

    followService.follow(user.getId(), entityType, entityId);

    // 触发关注事件
    Event event =
        new Event()
            .setTopic(TOPIC_LIKE)
            .setUserId(user.getId())
            .setEntityType(entityType)
            .setEntityId(entityId);
    // 目前只能关注人，代码先放着
    if (entityType == ENTITY_TYPE_POST) {
      event.setEntityUserId(discussPostService.findDiscussPostById(entityId).getUserId());
    } else if (entityType == ENTITY_TYPE_COMMENT) {
      event.setEntityUserId(commentService.findCommentById(entityId).getUserId());
    } else if (entityType == ENTITY_TYPE_USER) {
      event.setEntityUserId(entityId);
    }
    eventProducer.fireEvent(event);

    return CommunityUtil.getJSONString(0, "已关注");
  }

  @LoginRequired
  @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
  @ResponseBody
  public String unfollow(int entityType, int entityId) {
    User user = hostHolder.getUser();

    followService.unfollow(user.getId(), entityType, entityId);

    return CommunityUtil.getJSONString(0, "已取消关注");
  }

  @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
  public String getFolloweesUserPage(@PathVariable("userId") int userId, Page page, Model model) {
    User user = userService.findUserById(userId);
    if (user == null) {
      throw new RuntimeException("该用户不存在！");
    }

    model.addAttribute("user", user);
    page.setLimit(5);
    page.setPath("/followees/" + userId);
    page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

    List<Map<String, Object>> followeeUsers =
        followService.findFolloweeUser(userId, page.getOffset(), page.getLimit());
    if (followeeUsers != null) {
      for (Map<String, Object> followeeUser : followeeUsers) {
        User u = (User) followeeUser.get("user");
        followeeUser.put("hasFollowedUser", hasFollowedUser(u.getId()));
      }
    }

    model.addAttribute("followeeUsers", followeeUsers);

    return "/site/followee";
  }

  @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
  public String getFollowersPage(@PathVariable("userId") int userId, Page page, Model model) {
    User user = userService.findUserById(userId);
    if (user == null) {
      throw new RuntimeException("该用户不存在！");
    }

    model.addAttribute("user", user);
    page.setLimit(5);
    page.setPath("/followers/" + userId);
    page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

    List<Map<String, Object>> followers =
        followService.findFollower(ENTITY_TYPE_USER, userId, page.getOffset(), page.getLimit());
    if (followers != null) {
      for (Map<String, Object> follower : followers) {
        User u = (User) follower.get("user");
        follower.put("hasFollowedUser", hasFollowedUser(u.getId()));
      }
    }

    model.addAttribute("followers", followers);

    return "/site/follower";
  }

  private boolean hasFollowedUser(int userId) {
    User user = hostHolder.getUser();
    if (user == null) {
      return false;
    }
    return followService.findFollowStatute(user.getId(), ENTITY_TYPE_USER, userId);
  }
}
