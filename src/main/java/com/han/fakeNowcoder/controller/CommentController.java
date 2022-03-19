package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.annotation.LoginRequired;
import com.han.fakeNowcoder.entity.Comment;
import com.han.fakeNowcoder.entity.Event;
import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.event.EventProducer;
import com.han.fakeNowcoder.service.CommentService;
import com.han.fakeNowcoder.service.DiscussPostService;
import com.han.fakeNowcoder.util.CommunityCostant;
import com.han.fakeNowcoder.util.HostHolder;
import com.han.fakeNowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @author imhan
 */
@Controller
@RequestMapping(path = "/comment")
public class CommentController implements CommunityCostant {

  @Autowired private CommentService commentService;

  @Autowired private HostHolder hostHolder;

  @Autowired private DiscussPostService discussPostService;

  @Autowired private EventProducer eventProducer;

  @Autowired private RedisTemplate redisTemplate;

  @LoginRequired
  @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
  public String addComment(
      Model model, @PathVariable("discussPostId") int discussPostId, Comment comment) {
    User user = hostHolder.getUser();
    comment.setUserId(user.getId());
    comment.setStatus(0);
    comment.setCreateTime(new Date());
    commentService.addComment(comment);

    // 触发评论事件
    Event event =
        new Event()
            .setTopic(TOPIC_COMMENT)
            .setUserId(user.getId())
            .setEntityType(comment.getEntityType())
            .setEntityId(comment.getEntityId())
            .setData("discussPostId", discussPostId);
    if (comment.getEntityType() == ENTITY_TYPE_POST) {
      event.setEntityUserId(discussPostService.findDiscussPostById(discussPostId).getUserId());
    } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
      if (comment.getTargetId() == 0) {
        event.setEntityUserId(commentService.findCommentById(comment.getEntityId()).getUserId());
      } else {
        event.setEntityUserId(comment.getTargetId());
      }
    }
    eventProducer.fireEvent(event);

    // 如果评论了帖子，就修改了帖子的评论数量，需要触发帖子事件
    if (comment.getEntityType() == ENTITY_TYPE_POST) {
      // 触发发帖事件
      event =
          new Event()
              .setTopic(TOPIC_PUBLISH)
              .setUserId(user.getId())
              .setEntityType(ENTITY_TYPE_POST)
              .setEntityId(discussPostId);
      eventProducer.fireEvent(event);
    }

    // 如果评论了帖子，就修改了帖子的评论数量，需要计算分数
    if (comment.getEntityType() == ENTITY_TYPE_POST) {
      // 计算帖子分数
      String postScoreKey = RedisKeyUtil.getPostScoreKey();
      redisTemplate.opsForSet().add(postScoreKey, discussPostId);
    }

    return "redirect:/discuss/detail/" + discussPostId;
  }
}
