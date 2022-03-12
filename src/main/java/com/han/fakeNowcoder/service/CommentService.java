package com.han.fakeNowcoder.service;

import com.han.fakeNowcoder.dao.CommentMapper;
import com.han.fakeNowcoder.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

  @Autowired private CommentMapper commentMapper;

  public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit) {
    return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
  }

  public int findCommentRowsByEntity(int entityType, int entityId) {
    return commentMapper.selectCommentRowsByEntity(entityType, entityId);
  }
}
