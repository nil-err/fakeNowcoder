package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.annotation.LoginRequired;
import com.han.fakeNowcoder.entity.Event;
import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.event.EventProducer;
import com.han.fakeNowcoder.service.LikeService;
import com.han.fakeNowcoder.util.CommunityCostant;
import com.han.fakeNowcoder.util.CommunityUtil;
import com.han.fakeNowcoder.util.HostHolder;
import com.han.fakeNowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityCostant {

  @Autowired private HostHolder hostHolder;

  @Autowired private LikeService likeService;

  @Autowired private EventProducer eventProducer;

  @Autowired private RedisTemplate redisTemplate;

  @LoginRequired
  @RequestMapping(path = "/like", method = RequestMethod.POST)
  @ResponseBody
  public String like(int entityType, int entityId, int entityUserId, int discussPostId) {
    User user = hostHolder.getUser();
    // 点赞
    likeService.like(user.getId(), entityType, entityId, entityUserId);
    // 获取赞的数量
    long likeCount = likeService.findEntityLikeCount(entityType, entityId);
    // 获取是否点赞
    int likeStatus = likeService.finEntityLikeStatusOfUser(user.getId(), entityType, entityId);
    // 返回结果
    Map<String, Object> map = new HashMap<>();
    map.put("likeCount", likeCount);
    map.put("likeStatus", likeStatus);

    if (likeStatus == 1) {
      // 点赞之后，触发点赞事件
      Event event =
          new Event()
              .setTopic(TOPIC_LIKE)
              .setUserId(user.getId())
              .setEntityType(entityType)
              .setEntityId(entityId)
              .setEntityUserId(entityUserId)
              .setData("discussPostId", discussPostId);
      eventProducer.fireEvent(event);
    }

    // 如果点赞了帖子，就修改了帖子的点赞数量，需要计算分数
    if (entityType == ENTITY_TYPE_POST) {
      // 计算帖子分数
      String postScoreKey = RedisKeyUtil.getPostScoreKey();
      redisTemplate.opsForSet().add(postScoreKey, discussPostId);
    }

    return CommunityUtil.getJSONString(0, "操作成功", map);
  }
}
