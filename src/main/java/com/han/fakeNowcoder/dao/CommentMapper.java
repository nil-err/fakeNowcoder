package com.han.fakeNowcoder.dao;

import com.han.fakeNowcoder.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

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
  List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

  int selectCommentRowsByEntity(int entityType, int entityId);

  int insertCommet(Comment comment);

  Comment selectCommentById(int id);
}
