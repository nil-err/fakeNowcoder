package com.han.fakeNowcoder.service;

import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.util.CommunityCostant;
import com.han.fakeNowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityCostant {

  @Autowired private RedisTemplate redisTemplate;

  @Autowired private UserService userService;

  public void follow(int userId, int entityType, int entityId) {
    redisTemplate.execute(
        new SessionCallback() {
          @Override
          public Object execute(RedisOperations operations) throws DataAccessException {
            String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
            String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

            operations.multi();

            operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
            operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

            return operations.exec();
          }
        });
  }

  public void unfollow(int userId, int entityType, int entityId) {
    redisTemplate.execute(
        new SessionCallback() {
          @Override
          public Object execute(RedisOperations operations) throws DataAccessException {
            String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
            String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

            operations.multi();

            operations.opsForZSet().remove(followeeKey, entityId);
            operations.opsForZSet().remove(followerKey, userId);

            return operations.exec();
          }
        });
  }

  /**
   * 查询userId对entityType类型的关注数量
   *
   * @param userId
   * @param entityType
   * @return
   */
  public long findFolloweeCount(int userId, int entityType) {
    String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
    return redisTemplate.opsForZSet().zCard(followeeKey);
  }

  /**
   * 查询entityType的entityId实体的关注者数量
   *
   * @param entityType
   * @param entityId
   * @return
   */
  public long findFollowerCount(int entityType, int entityId) {
    String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
    return redisTemplate.opsForZSet().zCard(followerKey);
  }

  /**
   * 查询userId对entityType的entityId的关注状态
   *
   * @param userId
   * @param entityType
   * @param entityId
   * @return
   */
  public boolean findFollowStatute(int userId, int entityType, int entityId) {
    String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
    return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
  }

  /** 查询userId关注的用户 */
  public List<Map<String, Object>> findFolloweeUser(int userId, int offset, int limit) {
    String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
    Set<Integer> followeeIds =
        redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

    if (followeeIds == null) {
      return null;
    }

    List<Map<String, Object>> list = new ArrayList<>();
    for (int followeeId : followeeIds) {
      Map<String, Object> map = new HashMap<>();
      User user = userService.findUserById(followeeId);
      map.put("user", user);
      Double score = redisTemplate.opsForZSet().score(followeeKey, followeeId);
      map.put("followTime", new Date(score.longValue()));

      list.add(map);
    }

    return list;
  }

  /** 查询entityType的entityId实体的关注者 */
  public List<Map<String, Object>> findFollower(
      int entityType, int entityId, int offset, int limit) {
    String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
    Set<Integer> followerIds =
        redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

    if (followerIds == null) {
      return null;
    }

    List<Map<String, Object>> list = new ArrayList<>();
    for (int followerId : followerIds) {
      Map<String, Object> map = new HashMap<>();
      User user = userService.findUserById(followerId);
      map.put("user", user);
      Double score = redisTemplate.opsForZSet().score(followerKey, followerId);
      map.put("followTime", new Date(score.longValue()));

      list.add(map);
    }

    return list;
  }
}
