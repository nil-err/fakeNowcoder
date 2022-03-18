package com.han.fakeNowcoder.controller;

import com.alibaba.fastjson.JSONObject;
import com.han.fakeNowcoder.annotation.LoginRequired;
import com.han.fakeNowcoder.entity.Message;
import com.han.fakeNowcoder.entity.Page;
import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.service.MessageService;
import com.han.fakeNowcoder.service.UserService;
import com.han.fakeNowcoder.util.CommunityCostant;
import com.han.fakeNowcoder.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/notice")
public class NoticeController implements CommunityCostant {

  @Autowired private MessageService messageService;

  @Autowired private HostHolder hostHolder;

  @Autowired private UserService userService;

  @RequestMapping(path = "/list", method = RequestMethod.GET)
  public String getMessageList(Model model) {

    // 用户信息
    User user = hostHolder.getUser();

    // 查询评论类通知
    Message latestMessage = messageService.findLatestMessage(user.getId(), TOPIC_COMMENT);
    if (latestMessage != null) {

      Map<String, Object> messageVo = new HashMap<>();

      messageVo.put("message", latestMessage);

      String content = latestMessage.getContent();
      content = HtmlUtils.htmlUnescape(content);
      HashMap<String, Object> hashMap = JSONObject.parseObject(content, HashMap.class);

      messageVo.put("user", userService.findUserById((Integer) hashMap.get("userId")));
      messageVo.put("entityType", hashMap.get("entityType"));
      messageVo.put("entityId", hashMap.get("entityId"));
      messageVo.put("discussPostId", hashMap.get("discussPostId"));

      int noticeCount = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
      messageVo.put("noticeCount", noticeCount);

      int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(), TOPIC_COMMENT);
      messageVo.put("unreadNoticeCount", unreadNoticeCount);

      model.addAttribute("commentNotice", messageVo);
    }

    // 查询点赞类通知
    latestMessage = messageService.findLatestMessage(user.getId(), TOPIC_LIKE);
    if (latestMessage != null) {

      Map<String, Object> messageVo = new HashMap<>();

      messageVo.put("message", latestMessage);

      String content = latestMessage.getContent();
      content = HtmlUtils.htmlUnescape(content);
      HashMap<String, Object> hashMap = JSONObject.parseObject(content, HashMap.class);

      messageVo.put("user", userService.findUserById((Integer) hashMap.get("userId")));
      messageVo.put("entityType", hashMap.get("entityType"));
      messageVo.put("entityId", hashMap.get("entityId"));
      messageVo.put("discussPostId", hashMap.get("discussPostId"));

      int noticeCount = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
      messageVo.put("noticeCount", noticeCount);

      int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(), TOPIC_LIKE);
      messageVo.put("unreadNoticeCount", unreadNoticeCount);

      model.addAttribute("likeNotice", messageVo);
    }

    // 查询关注类通知
    latestMessage = messageService.findLatestMessage(user.getId(), TOPIC_FOLLOW);
    if (latestMessage != null) {

      Map<String, Object> messageVo = new HashMap<>();

      messageVo.put("message", latestMessage);

      String content = latestMessage.getContent();
      content = HtmlUtils.htmlUnescape(content);
      HashMap<String, Object> hashMap = JSONObject.parseObject(content, HashMap.class);

      messageVo.put("user", userService.findUserById((Integer) hashMap.get("userId")));
      messageVo.put("entityType", hashMap.get("entityType"));
      messageVo.put("entityId", hashMap.get("entityId"));

      int noticeCount = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
      messageVo.put("noticeCount", noticeCount);

      int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(), TOPIC_FOLLOW);
      messageVo.put("unreadNoticeCount", unreadNoticeCount);

      model.addAttribute("followNotice", messageVo);
    }

    // 查询未读通知数量
    int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(), null);
    model.addAttribute("unreadNoticeCount", unreadNoticeCount);

    // 未读消息总数
    int unreadMessagesCount = messageService.findUnreadMessagesCount(user.getId(), null);
    model.addAttribute("unreadMessagesCount", unreadMessagesCount);

    return "/site/notice";
  }

  @LoginRequired
  @RequestMapping(path = "/detail/{topic}", method = RequestMethod.GET)
  public String getNoticeDetial(@PathVariable("topic") String topic, Model model, Page page) {
    // 用户信息
    User user = hostHolder.getUser();

    // 分页信息
    page.setLimit(5);
    page.setPath("/notice/detail/" + topic);
    page.setRows(messageService.findNoticeCount(user.getId(), topic));

    List<Message> notices =
        messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
    List<Map<String, Object>> noticeList = new ArrayList<>();
    if (notices != null) {
      for (Message notice : notices) {
        Map<String, Object> map = new HashMap<>();
        // 通知
        map.put("notice", notice);
        // 内容
        String content = HtmlUtils.htmlUnescape(notice.getContent());
        HashMap<String, Object> hashMap = JSONObject.parseObject(content, HashMap.class);

        map.put("user", userService.findUserById((Integer) hashMap.get("userId")));
        map.put("entityType", hashMap.get("entityType"));
        map.put("entityId", hashMap.get("entityId"));
        map.put("discussPostId", hashMap.get("discussPostId"));

        // 通知作者
        map.put("fromUser", userService.findUserById(notice.getFromId()));
        noticeList.add(map);
      }
    }
    model.addAttribute("noticeList", noticeList);

    // 设置已读
    List<Integer> unreadNoticeIds = getUnreadMessageIds(notices);
    if (!unreadNoticeIds.isEmpty()) {
      messageService.readStatus(unreadNoticeIds);
    }

    return "/site/notice-detail";
  }

  private List<Integer> getUnreadMessageIds(List<Message> messages) {
    User user = hostHolder.getUser();
    List<Integer> ids = new ArrayList<>();
    if (messages != null) {
      for (Message message : messages) {
        if (user.getId() == message.getToId() && message.getStatus() == 0) {
          ids.add(message.getId());
        }
      }
    }
    return ids;
  }
}
