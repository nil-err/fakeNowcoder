package com.han.fakeNowcoder.quartz;

import com.han.fakeNowcoder.entity.DiscussPost;
import com.han.fakeNowcoder.service.DiscussPostService;
import com.han.fakeNowcoder.service.ElasticSearchService;
import com.han.fakeNowcoder.service.LikeService;
import com.han.fakeNowcoder.util.CommunityCostant;
import com.han.fakeNowcoder.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiscussPostScoreRefreshJob implements Job, CommunityCostant {

  public static final Logger logger = LoggerFactory.getLogger(DiscussPostScoreRefreshJob.class);

  // 牛科纪元
  public static final Date epoch;

  // 一天毫秒数
  public static final long msOfDay;

  static {
    msOfDay = 1000 * 360 * 24;
    try {
      epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:01");
    } catch (ParseException e) {
      throw new RuntimeException("初始化牛客纪元失败", e);
    }
  }

  @Autowired private RedisTemplate redisTemplate;

  @Autowired private DiscussPostService discussPostService;

  @Autowired private LikeService likeService;

  @Autowired private ElasticSearchService elasticSearchService;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    String postScoreKey = RedisKeyUtil.getPostScoreKey();
    BoundSetOperations operations = redisTemplate.boundSetOps(postScoreKey);

    if (operations.size() == 0) {
      logger.info("{任务取消} 没有需要刷新的帖子");
      return;
    }

    logger.info("{任务开始} 正在开始刷新帖子分数 ： " + operations.size());

    while (operations.size() > 0) {
      this.refresh((Integer) operations.pop());
    }

    logger.info("{任务结束} 帖子分数刷新完毕");
  }

  private void refresh(int discussPostId) {
    DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);

    if (discussPost == null) {
      logger.error("该帖子不存在 ： id = " + discussPostId);
      return;
    }

    // 是否加精
    boolean wonderful = discussPost.getStatus() == 1;
    // 评论数量
    int commentCount = discussPost.getCommentCount();
    // 点赞数
    long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);

    // 计算权重
    double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
    // 分数 = 帖子权重 + 距离天数
    double score =
        Math.log10(Math.max(w, 1))
            + (discussPost.getCreateTime().getTime() - epoch.getTime()) / msOfDay;

    // 更新帖子分数
    discussPostService.updateScore(discussPostId, score);

    // 更新搜索数据
    discussPost.setScore(score);
    elasticSearchService.saveDiscussPost(discussPost);
  }
}
