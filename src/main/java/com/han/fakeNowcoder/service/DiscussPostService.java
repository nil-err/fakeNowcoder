package com.han.fakeNowcoder.service;

import com.han.fakeNowcoder.dao.DiscussPostMapper;
import com.han.fakeNowcoder.entity.DiscussPost;
import com.han.fakeNowcoder.util.SensitiveFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author imhan
 */
@Service
public class DiscussPostService {

  public static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

  @Autowired private DiscussPostMapper discussPostMapper;

  @Autowired private SensitiveFilter sensitiveFilter;

  public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
    return discussPostMapper.selectDiscussPosts(userId, offset, limit);
  }

  public int findDiscussPostRows(int userId) {
    return discussPostMapper.selectDiscussPostRows(userId);
  }

  public int addDiscussPost(DiscussPost discussPost) {
    if (discussPost == null) {
      logger.info("增加帖子，参数为空");
      throw new IllegalArgumentException("参数不能为空");
    }

    // 转义html标记
    discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
    discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

    // 过滤敏感词
    discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
    discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

    return discussPostMapper.insertDiscussPost(discussPost);
  }

  public DiscussPost findDiscussPostById(int id) {
    return discussPostMapper.selectDiscussPostById(id);
  }
}
