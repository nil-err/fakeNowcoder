package com.han.fakeNowcoder.service;

import com.han.fakeNowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataStatisticsService {

  @Autowired private RedisTemplate redisTemplate;

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

  /** 将指定的IP计入UV */
  public void recoedUV(String ip) {
    String uvKey = RedisKeyUtil.getUVKey(dateFormat.format(new Date()));
    redisTemplate.opsForHyperLogLog().add(uvKey, ip);
  }

  /** 统计指定日期范围内的UV */
  public long getUV(Date startDate, Date endDate) {
    if (startDate == null || endDate == null || startDate.after(endDate)) {
      throw new IllegalArgumentException("请输入正确的时间段");
    }

    // 整理日期范围内的Key
    List<String> keyList = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    while (!calendar.getTime().after(endDate)) {
      String key = RedisKeyUtil.getUVKey(dateFormat.format(calendar.getTime()));
      keyList.add(key);
      calendar.add(Calendar.DATE, 1);
    }

    // 合并数据
    String redisKey =
        RedisKeyUtil.getUVKey(dateFormat.format(startDate), dateFormat.format(endDate));
    redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());

    // 返回结果
    return redisTemplate.opsForHyperLogLog().size(redisKey);
  }

  /** 将指定的用户计入DAU */
  public void recoedDAU(int userId) {
    String dauKey = RedisKeyUtil.getDAUKey(dateFormat.format(new Date()));
    redisTemplate.opsForValue().setBit(dauKey, userId, true);
  }

  /** 统计指定日期范围内的DAU */
  public long getDAU(Date startDate, Date endDate) {
    if (startDate == null || endDate == null || startDate.after(endDate)) {
      throw new IllegalArgumentException("请输入正确的时间段");
    }

    // 整理日期范围内的Key
    List<byte[]> keyList = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    while (!calendar.getTime().after(endDate)) {
      String key = RedisKeyUtil.getDAUKey(dateFormat.format(calendar.getTime()));
      keyList.add(key.getBytes());
      calendar.add(Calendar.DATE, 1);
    }

    // 合并数据
    String redisKey =
        RedisKeyUtil.getUVKey(dateFormat.format(startDate), dateFormat.format(endDate));

    Object o =
        redisTemplate.execute(
            new RedisCallback() {
              @Override
              public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(
                    RedisStringCommands.BitOperation.OR,
                    redisKey.getBytes(),
                    keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
              }
            });

    // 返回结果
    return (long) o;
  }
}
