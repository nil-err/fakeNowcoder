package com.han.fakeNowcoder.service;

import com.han.fakeNowcoder.dao.MessageMapper;
import com.han.fakeNowcoder.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

  @Autowired private MessageMapper messageMapper;

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
}
