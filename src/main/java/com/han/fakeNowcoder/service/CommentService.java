package com.han.fakeNowcoder.service;

import com.han.fakeNowcoder.dao.CommentMapper;
import com.han.fakeNowcoder.entity.Comment;
import com.han.fakeNowcoder.util.CommunityCostant;
import com.han.fakeNowcoder.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author imhan
 */
@Service
public class CommentService implements CommunityCostant {

  @Autowired private CommentMapper commentMapper;

  @Autowired private SensitiveFilter sensitiveFilter;

  @Autowired private DiscussPostService discussPostService;

  public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit) {
    return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
  }

  public int findCommentRowsByEntity(int entityType, int entityId) {
    return commentMapper.selectCommentRowsByEntity(entityType, entityId);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public int addComment(Comment comment) {
    if (comment == null) {
      throw new IllegalArgumentException("参数不能为空");
    }

    // 添加评论
    comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
    comment.setContent(sensitiveFilter.filter(comment.getContent()));
    int i = commentMapper.insertCommet(comment);

    // 更新帖子评论数
    if (comment.getEntityType() == ENTITY_TYPE_POST) {
      int commentCount =
          commentMapper.selectCommentRowsByEntity(comment.getEntityType(), comment.getEntityId());
      discussPostService.updateCommentCount(comment.getEntityId(), commentCount);
    }

    return i;
  }

  public Comment findCommentById(int id) {
    return commentMapper.selectCommentById(id);
  }

  public List<Comment> findCommentByUser(int userId, int offset, int limit) {
    return commentMapper.selectCommentByUser(userId, offset, limit);
  }

  public int findCommentRowsByUser(int userId) {
    return commentMapper.selectCommentRowsByUser(userId);
  }
}
