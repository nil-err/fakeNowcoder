package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.annotation.LoginRequired;
import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.service.LikeService;
import com.han.fakeNowcoder.util.CommunityUtil;
import com.han.fakeNowcoder.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

  @Autowired private HostHolder hostHolder;

  @Autowired private LikeService likeService;

  @LoginRequired
  @RequestMapping(path = "/like", method = RequestMethod.POST)
  @ResponseBody
  public String like(int entityType, int entityId, int entityUserId) {
    User user = hostHolder.getUser();
    // 点赞
    likeService.like(user.getId(), entityType, entityId, entityUserId);
    // 获取赞的数量
    long likeCount = likeService.findEntityLikeCount(entityType, entityId);
    // 获取是否点赞
    int status = likeService.finEntityLikeStatusOfUser(user.getId(), entityType, entityId);
    // 返回结果
    Map<String, Object> map = new HashMap<>();
    map.put("likeCount", likeCount);
    map.put("status", status);

    return CommunityUtil.getJSONString(0, "操作成功", map);
  }
}
