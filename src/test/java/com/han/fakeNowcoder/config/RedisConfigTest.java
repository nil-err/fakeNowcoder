package com.han.fakeNowcoder.config;

import com.han.fakeNowcoder.FakeNowcoderApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = FakeNowcoderApplication.class)
class RedisConfigTest {

  @Autowired private RedisTemplate redisTemplate;

  @Test
  public void testStrings() {
    String redisKey = "test:count";
    redisTemplate.opsForValue().set(redisKey, 1);

    System.out.println(redisTemplate.opsForValue().get(redisKey));
    System.out.println(redisTemplate.opsForValue().increment(redisKey));
    System.out.println(redisTemplate.opsForValue().decrement(redisKey));
  }

  @Test
  public void testHashes() {
    String redisKey = "test:user";
    redisTemplate.opsForHash().put(redisKey, "id", 1);
    redisTemplate.opsForHash().put(redisKey, "name", "zhangsan");

    System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
    System.out.println(redisTemplate.opsForHash().get(redisKey, "name"));
  }

  @Test
  public void testLists() {
    String redisKey = "test:ids";
    redisTemplate.opsForList().leftPush(redisKey, 101);
    redisTemplate.opsForList().leftPush(redisKey, 102);
    redisTemplate.opsForList().leftPush(redisKey, 103);
    redisTemplate.opsForList().leftPush(redisKey, 106);
    redisTemplate.opsForList().leftPush(redisKey, 107);
    redisTemplate.opsForList().leftPush(redisKey, 108);
    redisTemplate.opsForList().leftPush(redisKey, 109);

    System.out.println(redisTemplate.opsForList().size(redisKey));
    System.out.println(redisTemplate.opsForList().index(redisKey, 2));
    System.out.println(redisTemplate.opsForList().range(redisKey, 2, 4));

    System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    System.out.println(redisTemplate.opsForList().rightPop(redisKey));
  }

  @Test
  public void testSets() {
    String redisKey = "test:teacher";
    redisTemplate.opsForSet().add(redisKey, "张飞", "关羽", "刘备", "诸葛亮", "周瑜", "曹操");

    System.out.println(redisTemplate.opsForSet().size(redisKey));
    System.out.println(redisTemplate.opsForSet().members(redisKey));
    System.out.println(redisTemplate.opsForSet().pop(redisKey));
    System.out.println(redisTemplate.opsForSet().members(redisKey));
  }

  @Test
  public void testSortedSets() {
    String redisKey = "test:student";
    redisTemplate.opsForZSet().add(redisKey, "张飞", 90);
    redisTemplate.opsForZSet().add(redisKey, "关羽", 70);
    redisTemplate.opsForZSet().add(redisKey, "刘备", 60);
    redisTemplate.opsForZSet().add(redisKey, "诸葛亮", 80);
    redisTemplate.opsForZSet().add(redisKey, "周瑜", 80);
    redisTemplate.opsForZSet().add(redisKey, "曹操", 100);

    System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
    System.out.println(redisTemplate.opsForZSet().score(redisKey, "刘备"));
    System.out.println(redisTemplate.opsForZSet().rank(redisKey, "刘备"));
    System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "刘备"));
    System.out.println(redisTemplate.opsForZSet().range(redisKey, 0, 2));
    System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));
  }

  @Test
  public void testKeys() {
    redisTemplate.delete("test:user");

    System.out.println(redisTemplate.hasKey("test:teacher"));

    redisTemplate.expire("test:teacher", 10, TimeUnit.SECONDS);
  }

  @Test
  public void testBoundOperations() {
    String redisKey = "test:count";
    BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
    System.out.println(operations.increment());
    System.out.println(operations.get());
    System.out.println(operations.decrement());
  }

  /** 测试Redis编程式事务 */
  @Test
  public void testTransacrional() {
    Object obj =
        redisTemplate.execute(
            new SessionCallback() {
              @Override
              public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";

                operations.multi();

                operations.opsForSet().add(redisKey, "张飞");
                operations.opsForSet().add(redisKey, "关羽");
                operations.opsForSet().add(redisKey, "刘备");
                operations.opsForSet().add(redisKey, "诸葛亮");
                operations.opsForSet().add(redisKey, "周瑜");
                operations.opsForSet().add(redisKey, "曹操");

                /* redis事务中间做查询结果不对，因为在事务提交之后，才会把命令发送给redis服务器执行 */
                System.out.println(operations.opsForSet().members(redisKey));

                return operations.exec();
              }
            });
    System.out.println(obj);
  }

  /** 统计20万个重复数据的独立总数 */
  @Test
  public void testHyperLogLog() {
    String redisKey = "test:hll:01";

    for (int i = 0; i < 100000; i++) {
      redisTemplate.opsForHyperLogLog().add(redisKey, i);
    }

    for (int i = 0; i < 100000; i++) {
      int r = (int) (Math.random() * 100000 + 1);
      redisTemplate.opsForHyperLogLog().add(redisKey, r);
    }

    System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));
  }

  /** 将3组数据合并，再统计合并后的重复数据独立总数 */
  @Test
  public void testHyperLogLogUnion() {
    String redisKey2 = "test:hll:02";
    for (int i = 1; i <= 10000; i++) {
      redisTemplate.opsForHyperLogLog().add(redisKey2, i);
    }
    String redisKey3 = "test:hll:03";
    for (int i = 5001; i <= 15000; i++) {
      redisTemplate.opsForHyperLogLog().add(redisKey3, i);
    }
    String redisKey4 = "test:hll:04";
    for (int i = 10001; i <= 20000; i++) {
      redisTemplate.opsForHyperLogLog().add(redisKey4, i);
    }

    String unionKey = "test:hll:union";
    redisTemplate.opsForHyperLogLog().union(unionKey, redisKey2, redisKey3, redisKey4);

    System.out.println(redisTemplate.opsForHyperLogLog().size(unionKey));
  }

  /** 统计一组数据的布尔值 */
  @Test
  public void testBitmap() {
    String redisKey = "test:bm:01";

    // 记录
    redisTemplate.opsForValue().setBit(redisKey, 1, true);
    redisTemplate.opsForValue().setBit(redisKey, 4, true);
    redisTemplate.opsForValue().setBit(redisKey, 7, true);

    // 查询
    System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
    System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
    System.out.println(redisTemplate.opsForValue().getBit(redisKey, 2));

    // 统计
    Object o =
        redisTemplate.execute(
            new RedisCallback() {
              @Override
              public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
              }
            });
    System.out.println(o);
  }

  // 统计3组数据的布尔值，并作OR运算
  @Test
  public void testBitmapOperation() {
    String redisKey2 = "test:bm:02";
    redisTemplate.opsForValue().setBit(redisKey2, 0, true);
    redisTemplate.opsForValue().setBit(redisKey2, 1, true);
    redisTemplate.opsForValue().setBit(redisKey2, 2, true);
    String redisKey3 = "test:bm:03";
    redisTemplate.opsForValue().setBit(redisKey3, 2, true);
    redisTemplate.opsForValue().setBit(redisKey3, 3, true);
    redisTemplate.opsForValue().setBit(redisKey3, 4, true);
    String redisKey4 = "test:bm:04";
    redisTemplate.opsForValue().setBit(redisKey4, 4, true);
    redisTemplate.opsForValue().setBit(redisKey4, 5, true);
    redisTemplate.opsForValue().setBit(redisKey4, 6, true);

    String redisKey = "test:bm:or";

    // 统计
    Object o =
        redisTemplate.execute(
            new RedisCallback() {
              @Override
              public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(
                    RedisStringCommands.BitOperation.OR,
                    redisKey.getBytes(),
                    redisKey2.getBytes(),
                    redisKey3.getBytes(),
                    redisKey4.getBytes());
                return connection.bitCount(redisKey.getBytes());
              }
            });
    System.out.println(o);
  }
}
