package com.han.fakeNowcoder.config;

import com.han.fakeNowcoder.FakeNowcoderApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
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

                redisTemplate.opsForSet().add(redisKey, "张飞");
                redisTemplate.opsForSet().add(redisKey, "关羽");
                redisTemplate.opsForSet().add(redisKey, "刘备");
                redisTemplate.opsForSet().add(redisKey, "诸葛亮");
                redisTemplate.opsForSet().add(redisKey, "周瑜");
                redisTemplate.opsForSet().add(redisKey, "曹操");

                /* redis事务中间做查询结果不对，因为在事务提交之后，才会把命令发送给redis服务器执行 */
                System.out.println(redisTemplate.opsForSet().members(redisKey));

                return operations.exec();
              }
            });
    System.out.println(obj);
  }
}
