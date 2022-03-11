package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.annotation.LoginRequired;
import com.han.fakeNowcoder.entity.DiscussPost;
import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.service.DiscussPostService;
import com.han.fakeNowcoder.service.UserService;
import com.han.fakeNowcoder.util.CommunityUtil;
import com.han.fakeNowcoder.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController {

  @Autowired private DiscussPostService discussPostService;

  @Autowired private HostHolder hostHolder;

  @Autowired private UserService userService;

  @LoginRequired
  @RequestMapping(path = "/add", method = RequestMethod.POST)
  @ResponseBody
  public String addDiscussPost(String title, String content) {
    User user = hostHolder.getUser();
    if (user == null) {
      return CommunityUtil.getJSONString(403, "你还没有登录！");
    }

    DiscussPost discussPost = new DiscussPost();
    discussPost.setUserId(user.getId());
    discussPost.setTitle(title);
    discussPost.setContent(content);
    discussPost.setType(0);
    discussPost.setStatus(0);
    discussPost.setCreateTime(new Date());
    discussPost.setCommentCount(0);
    discussPost.setScore(0);
    discussPostService.addDiscussPost(discussPost);

    // 程序出错情况，之后统一处理

    return CommunityUtil.getJSONString(0, "发布成功！");
  }
}
