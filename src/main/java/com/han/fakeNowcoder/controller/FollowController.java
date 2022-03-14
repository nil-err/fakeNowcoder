package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.annotation.LoginRequired;
import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.service.FollowService;
import com.han.fakeNowcoder.util.CommunityUtil;
import com.han.fakeNowcoder.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController {

  @Autowired private FollowService followService;

  @Autowired private HostHolder hostHolder;

  @LoginRequired
  @RequestMapping(path = "/follow", method = RequestMethod.POST)
  @ResponseBody
  public String follow(int entityType, int entityId) {
    User user = hostHolder.getUser();

    followService.follow(user.getId(), entityType, entityId);

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
}
