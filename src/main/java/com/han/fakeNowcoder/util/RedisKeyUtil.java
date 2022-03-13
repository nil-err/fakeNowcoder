package com.han.fakeNowcoder.util;

public class RedisKeyUtil {

  private static final String SPLIT = ":";

  private static final String PREFIX_ENTITY_LIKE = "like:entity";

  private static final String PREFIX_USER_OBTAINED_LIKE = "liker:user";

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
}
