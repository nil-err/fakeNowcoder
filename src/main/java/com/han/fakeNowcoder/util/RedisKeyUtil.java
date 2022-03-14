package com.han.fakeNowcoder.util;

public class RedisKeyUtil {

  private static final String SPLIT = ":";

  private static final String PREFIX_ENTITY_LIKE = "like:entity";

  private static final String PREFIX_USER_OBTAINED_LIKE = "liker:user";

  private static final String PERFIX_FOLLOWEE = "followee";

  private static final String PERFIX_FOLLOWER = "follower";

  /**
   * 某个实体的赞 <br>
   * Key：like:entity:entityType:entityId <br>
   * Value: Set(userId) <br>
   */
  public static String getEntityLikeKey(int entityType, int entityId) {
    return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
  }

  /**
   * 某个用户收到的赞 <br>
   * Key：like:entity:userId <br>
   * Value: int <br>
   */
  public static String getUserObtainedLikeKey(int userId) {
    return PREFIX_USER_OBTAINED_LIKE + SPLIT + userId;
  }

  /**
   * 某个用户关注的实体 <br>
   * Key：followee:userId:entityType <br>
   * Value: zset(entityId,now) <br>
   */
  public static String getFolloweeKey(int userId, int entityType) {
    return PERFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
  }

  /**
   * 某个实体的粉丝 <br>
   * Key：followee:entityType:entityId <br>
   * Value: zset(userId,now) <br>
   */
  public static String getFollowerKey(int entityType, int entityId) {
    return PERFIX_FOLLOWEE + SPLIT + entityType + SPLIT + entityId;
  }
}
