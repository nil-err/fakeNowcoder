package com.han.fakeNowcoder.service;

import com.han.fakeNowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

  @Autowired private RedisTemplate redisTemplate;

  /**
   * 点赞
   *
   * @param userId 当前用户id
   * @param entityType 实体类型，帖子、评论
   * @param entityId 实体Id
   */
  public void like(int userId, int entityType, int entityId) {
    String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
    Boolean liked = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
    if (liked) {
      redisTemplate.opsForSet().remove(entityLikeKey, userId);
    } else {
      redisTemplate.opsForSet().add(entityLikeKey, userId);
    }
  }

  /**
   * 查询某个实体赞的数量
   *
   * @param entityType 实体类型，帖子、评论
   * @param entityId 实体Id
   * @return 返回赞的数量
   */
  public long findEntityLikeCount(int entityType, int entityId) {
    String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
    return redisTemplate.opsForSet().size(entityLikeKey);
  }

  /**
   * 查询用户对帖子点赞状态
   *
   * @param userId 当前用户id
   * @param entityType 实体类型，帖子、评论
   * @param entityId 实体Id
   * @return 返回1表示已点赞，0 未点赞， 可以后续扩展为点踩
   */
  public int finEntityLikeStatusOfUser(int userId, int entityType, int entityId) {
    String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
    return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
  }
}
