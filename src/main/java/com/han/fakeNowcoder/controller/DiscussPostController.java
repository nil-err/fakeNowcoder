package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.annotation.LoginRequired;
import com.han.fakeNowcoder.entity.Comment;
import com.han.fakeNowcoder.entity.DiscussPost;
import com.han.fakeNowcoder.entity.Page;
import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.service.CommentService;
import com.han.fakeNowcoder.service.DiscussPostService;
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

import java.util.*;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController implements CommunityCostant {

  @Autowired private DiscussPostService discussPostService;

  @Autowired private HostHolder hostHolder;

  @Autowired private UserService userService;

  @Autowired private CommentService commentService;

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

  @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
  public String getDiscussPost(
      @PathVariable("discussPostId") int discussPostId, Model model, Page page) {
    DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
    model.addAttribute("discussPost", discussPost);

    User user = userService.findUserById(discussPost.getUserId());
    model.addAttribute("user", user);

    // 分页信息
    page.setLimit(5);
    page.setPath("/discuss/detail/" + discussPostId);
    page.setRows(discussPost.getCommentCount());

    // 评论：帖子的评论
    // 回复：评论的评论
    // 评论列表
    List<Comment> commentList =
        commentService.findCommentByEntity(
            ENTITY_TYPE_POST, discussPost.getId(), page.getOffset(), page.getLimit());
    // 评论VO列表
    List<Map<String, Object>> commentVoList = new ArrayList<>();
    if (commentList != null) {
      for (Comment comment : commentList) {
        // 评论VO
        Map<String, Object> commentVo = new HashMap<>();
        // 评论
        commentVo.put("comment", comment);
        // 作者
        commentVo.put("user", userService.findUserById(comment.getUserId()));

        // 回复列表
        List<Comment> replyList =
            commentService.findCommentByEntity(
                ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
        // 回复VO列表
        List<Map<String, Object>> replyVoList = new ArrayList<>();
        if (replyList != null) {
          for (Comment reply : replyList) {
            // 回复VO
            Map<String, Object> replyVo = new HashMap<>();
            // 回复
            replyVo.put("reply", reply);
            // 回复作者
            replyVo.put("replyUser", userService.findUserById(reply.getUserId()));
            // 回复目标
            User target =
                reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
            replyVo.put("target", target);
            replyVoList.add(replyVo);
          }
        }

        commentVo.put("replys", replyVoList);

        // 回复数量
        int replyCount =
            commentService.findCommentRowsByEntity(ENTITY_TYPE_COMMENT, comment.getId());
        commentVo.put("replyCount", replyCount);

        commentVoList.add(commentVo);
      }
    }
    model.addAttribute("comments", commentVoList);

    return "/site/discuss-detail";
  }
}
