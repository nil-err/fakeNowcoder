package com.han.fakeNowcoder.service;

import com.han.fakeNowcoder.dao.MessageMapper;
import com.han.fakeNowcoder.entity.Message;
import com.han.fakeNowcoder.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class MessageService {

  @Autowired private MessageMapper messageMapper;

  @Autowired private SensitiveFilter sensitiveFilter;

  /** 查询会话列表,每个会话只有最后一条私信 */
  public List<Message> findConversations(int userId, int offset, int limit) {
    return messageMapper.selectConversations(userId, offset, limit);
  }

  /** 查询会话数量 */
  public int findConversationsCount(int userId) {
    return messageMapper.selectConversationsCount(userId);
  }

  /** 查询某个会话 */
  public List<Message> findMessages(String conversationId, int offset, int limit) {
    return messageMapper.selectMessages(conversationId, offset, limit);
  }

  /** 查询某个会话消息数量 */
  public int findMessagesCount(String conversationId) {
    return messageMapper.selectMessagesCount(conversationId);
  }

  /** 查询未读私信数量 */
  public int findUnreadMessagesCount(int userId, String conversationId) {
    return messageMapper.selectUnreadMessagesCount(userId, conversationId);
  }

  /** 新增消息 */
  public int addMessage(Message message) {
    message.setContent((HtmlUtils.htmlEscape(message.getContent())));
    message.setContent(sensitiveFilter.filter(message.getContent()));
    return messageMapper.insertMessage(message);
  }

  /** 修改消息已读状态 */
  public int readStatus(List<Integer> ids) {
    return messageMapper.updateStatus(ids, 1);
  }

  /** 修改消息为删除状态 */
  public int deleteMessage(int id) {
    return messageMapper.updateStatus(Arrays.asList(new Integer[] {id}), 2);
  }
}
