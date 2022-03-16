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
import org.springframework.beans.factory.annotation.Autowired;
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

    return "redirect:/discuss/detail/" + discussPostId;
  }
}
