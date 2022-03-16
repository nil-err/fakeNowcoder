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

  /** 新增消息 */
  int insertMessage(@Param("message") Message message);

  /** 修改消息状态 */
  int updateStatus(@Param("ids") List<Integer> ids, @Param("status") int status);

  /** 查询某个主题下最新的通知 */
  Message selectLatestMessage(
      @Param("userId") int userId, @Param("conversationId") String conversationId);

  /**
   * 查询某个主题的通知数量 <br>
   * 可以直接用 查询未读私信数量实现 selectMessagesCount
   */
  int selectNoticeCount(@Param("userId") int userId, @Param("topic") String topic);

  /**
   * 查询某个主题未读通知数量 <br>
   * 可以直接用 查询未读私信数量实现 selectUnreadMessagesCount
   */
  int selectUnreadNoticeCount(@Param("userId") int userId, @Param("topic") String topic);

  /** 查询某个主题通知列表 */
  List<Message> selectNotices(
      @Param("userId") int userId,
      @Param("conversationId") String conversationId,
      @Param("offset") int offset,
      @Param("limit") int limit);
}
