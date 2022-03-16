package com.han.fakeNowcoder.dao;

import com.han.fakeNowcoder.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author imhan
 */
@Mapper
public interface CommentMapper {

  /**
   * 根据对象实体查找评论，用于根据当前帖子查找帖子的评论、回复
   *
   * @param entityType 被评论的实体类型，评论、回复
   * @param entityId 被评论的实体Id
   * @param offset
   * @param limit
   * @return
   */
  List<Comment> selectCommentByEntity(
      @Param("entityType") int entityType,
      @Param("entityId") int entityId,
      @Param("offset") int offset,
      @Param("limit") int limit);

  int selectCommentRowsByEntity(
      @Param("entityType") int entityType, @Param("entityId") int entityId);

  int insertCommet(@Param("comment") Comment comment);

  Comment selectCommentById(@Param("id") int id);

  List<Comment> selectCommentByUser(
      @Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

  int selectCommentRowsByUser(@Param("userId") int userId);
}
