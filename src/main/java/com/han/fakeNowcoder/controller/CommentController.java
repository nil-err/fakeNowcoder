package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.annotation.LoginRequired;
import com.han.fakeNowcoder.entity.Comment;
import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.service.CommentService;
import com.han.fakeNowcoder.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping(path = "/comment")
public class CommentController {

  @Autowired private CommentService commentService;

  @Autowired private HostHolder hostHolder;

  @LoginRequired
  @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
  public String addComment(
      Model model, @PathVariable("discussPostId") int discussPostId, Comment comment) {
    User user = hostHolder.getUser();
    comment.setUserId(user.getId());
    comment.setStatus(0);
    comment.setCreateTime(new Date());
    commentService.addComment(comment);

    return "redirect:/discuss/detail/" + discussPostId;
  }
}
