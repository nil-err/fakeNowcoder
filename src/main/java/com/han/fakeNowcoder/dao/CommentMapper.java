package com.han.fakeNowcoder.dao;

import com.han.fakeNowcoder.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

  List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

  int selectCommentRowsByEntity(int entityType, int entityId);

  int insertCommet(Comment comment);
}
