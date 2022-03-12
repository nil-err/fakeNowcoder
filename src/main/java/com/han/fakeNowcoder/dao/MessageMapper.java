package com.han.fakeNowcoder.dao;

import com.han.fakeNowcoder.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {

  /** 查询会话列表,每个会话只有最后一条私信 */
  List<Message> selectConversations(
      @Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

  /** 查询会话数量 */
  int selectConversationsCount(@Param("userId") int userId);

  /** 查询某个会话 */
  List<Message> selectMessages(
      @Param("conversationId") String conversationId,
      @Param("offset") int offset,
      @Param("limit") int limit);

  /** 查询某个会话消息数量 */
  int selectMessagesCount(@Param("conversationId") String conversationId);

  /** 查询未读私信数量 */
  int selectUnreadMessagesCount(
      @Param("userId") int userId, @Param("conversationId") String conversationId);
}
