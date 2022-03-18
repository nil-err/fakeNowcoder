package com.han.fakeNowcoder.util;

public class RedisKeyUtil {

  private static final String SPLIT = ":";

  private static final String PREFIX_ENTITY_LIKE = "like:entity";

  private static final String PREFIX_USER_OBTAINED_LIKE = "liker:user";

  private static final String PREFIX_FOLLOWEE = "followee";

  private static final String PREFIX_FOLLOWER = "follower";

  private static final String PREFIX_KAPTCHA = "kaptcha";

  private static final String PREFIX_LOGIN_TICKET = "login:ticket";

  private static final String PREFIX_USER_ID = "user:id";

  private static final String PREFIX_UV = "uv";

  private static final String PREFIX_DAU = "dau";

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
    return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
  }

  /**
   * 某个实体的粉丝 <br>
   * Key：followee:entityType:entityId <br>
   * Value: zset(userId,now) <br>
   */
  public static String getFollowerKey(int entityType, int entityId) {
    return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
  }

  // 登录验证码
  public static String getKaptchaKey(String owner) {
    return PREFIX_KAPTCHA + SPLIT + owner;
  }

  // 登陆凭证
  public static String getLoginTicketKey(String ticket) {
    return PREFIX_LOGIN_TICKET + SPLIT + ticket;
  }

  // 用户
  public static String getUserIdKey(int userId) {
    return PREFIX_USER_ID + SPLIT + userId;
  }

  // 单日UV
  public static String getUVKey(String date) {
    return PREFIX_UV + SPLIT + date;
  }

  // 区间UV

  public static String getUVKey(String startDate, String endDate) {
    return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
  }

  // 单日DAU
  public static String getDAUKey(String date) {
    return PREFIX_DAU + SPLIT + date;
  }

  // 区间DAU

  public static String getDAUKey(String startDate, String endDate) {
    return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
  }
}
