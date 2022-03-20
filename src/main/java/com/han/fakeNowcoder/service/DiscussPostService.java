package com.han.fakeNowcoder.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.han.fakeNowcoder.dao.DiscussPostMapper;
import com.han.fakeNowcoder.entity.DiscussPost;
import com.han.fakeNowcoder.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author imhan
 */
@Service
public class DiscussPostService {

  public static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

  @Autowired private DiscussPostMapper discussPostMapper;

  @Autowired private SensitiveFilter sensitiveFilter;

  @Value("${caffeine.posts.max-size}")
  public int maxSize;

  @Value("${caffeine.posts.expire-seconds}")
  public int expireSeconds;

  // Caffeine核心接口：Cache，两个常用子接口LoadingCache、AsyncLoadingCache

  // 帖子列表缓存
  private LoadingCache<String, List<DiscussPost>> discussPostLoadingCache;

  // 帖子行数缓存
  private LoadingCache<Integer, Integer> discussPostRowsLoadingCache;

  @PostConstruct
  public void initCache() {
    // 初始化帖子列表缓存
    discussPostLoadingCache =
        Caffeine.newBuilder()
            .maximumSize(maxSize)
            .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
            .build(
                new CacheLoader<String, List<DiscussPost>>() {
                  @Override
                  public @Nullable List<DiscussPost> load(String key) throws Exception {
                    if (StringUtils.isBlank(key)) {
                      throw new IllegalArgumentException("参数有误！");
                    }
                    String[] s = key.split(":");
                    if (s == null || s.length != 2) {
                      throw new IllegalArgumentException("参数有误！");
                    }

                    int offset = Integer.valueOf(s[0]);
                    int limit = Integer.valueOf(s[1]);

                    // todo
                    // 可以在此加二级缓存，Redis

                    // 访问数据库
                    logger.debug("load posts list from DB.");
                    return discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                  }
                });

    // 初始化帖子总数缓存
    discussPostRowsLoadingCache =
        Caffeine.newBuilder()
            .maximumSize(maxSize)
            .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
            .build(
                new CacheLoader<Integer, Integer>() {
                  @Override
                  public @Nullable Integer load(Integer key) throws Exception {
                    if (key == null || key != 0) {
                      throw new IllegalArgumentException("参数有误！");
                    }
                    // todo
                    // 可以在此加二级缓存，Redis

                    // 访问数据库
                    logger.debug("load post rows from DB.");
                    return discussPostMapper.selectDiscussPostRows(key);
                  }
                });
  }

  public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode) {
    // 只缓存首页热门排序的帖子
    if (userId == 0 && orderMode == 1) {
      return discussPostLoadingCache.get(offset + ":" + limit);
    }

    logger.debug("load posts list from DB.");
    return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
  }

  public int findDiscussPostRows(int userId) {
    // 只缓存首页查询帖子总数
    if (userId == 0) {
      return discussPostRowsLoadingCache.get(userId);
    }

    logger.debug("load post rows from DB.");
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

  public int updateCommentCount(int id, int commentCount) {
    return discussPostMapper.updateCommentCount(id, commentCount);
  }

  public int updateType(int id, int type) {
    return discussPostMapper.updateType(id, type);
  }

  public int updateStatus(int id, int status) {
    return discussPostMapper.updateStatus(id, status);
  }

  public int updateScore(int id, double score) {
    return discussPostMapper.updateScore(id, score);
  }
}
