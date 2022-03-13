package com.han.fakeNowcoder.service;

import com.han.fakeNowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
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
   * @param entityUserId 当前实体的作者，即被赞的人
   */
  public void like(int userId, int entityType, int entityId, int entityUserId) {
    /*
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        Boolean liked = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if (liked) {
          redisTemplate.opsForSet().remove(entityLikeKey, userId);
        } else {
          redisTemplate.opsForSet().add(entityLikeKey, userId);
        }
    */
    // 同时处理实体的赞的用户收到的赞，用事务
    redisTemplate.execute(
        new SessionCallback() {
          @Override
          public Object execute(RedisOperations operations) throws DataAccessException {

            String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);

            String userObtainedLikeKey = RedisKeyUtil.getUserObtainedLikeKey(entityUserId);

            Boolean liked = redisTemplate.opsForSet().isMember(entityLikeKey, userId);

            redisTemplate.multi();

            if (liked) {
              redisTemplate.opsForSet().remove(entityLikeKey, userId);
              redisTemplate.opsForValue().decrement(userObtainedLikeKey);
            } else {
              redisTemplate.opsForSet().add(entityLikeKey, userId);
              redisTemplate.opsForValue().increment(userObtainedLikeKey);
            }

            return redisTemplate.exec();
          }
        });
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

  /**
   * 查询某个用户获得赞的数量
   *
   * @param entityUserId 当前实体的作者，即被赞的人
   * @return 返回赞的数量
   */
  public int findUserObtainedLikeCount(int entityUserId) {
    String userObtainedLikeKey = RedisKeyUtil.getUserObtainedLikeKey(entityUserId);
    Integer count = (Integer) redisTemplate.opsForValue().get(userObtainedLikeKey);
    return count == null ? 0 : count.intValue();
  }
}
