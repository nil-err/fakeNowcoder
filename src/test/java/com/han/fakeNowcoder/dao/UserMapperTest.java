package com.han.fakeNowcoder.dao;

import com.han.fakeNowcoder.FakeNowcoderApplication;
import com.han.fakeNowcoder.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = FakeNowcoderApplication.class)
class UserMapperTest {

  @Autowired private UserMapper userMapper;

  @Test
  void selectById() {
    User user = userMapper.selectById(150);
    System.out.println(user);
  }

  @Test
  void selectByName() {
    User user = userMapper.selectByName("liubei");
    System.out.println(user);
  }

  @Test
  void selectByEmail() {
    User user = userMapper.selectByEmail("nowcoder101@sina.com");
    System.out.println(user);
  }

  @Test
  void insertUser() {
    User user = new User();
    user.setUsername("xiaohua");
    user.setPassword("123456");
    user.setSalt("abc");
    user.setEmail("ACA@han.com");
    user.setHeaderUrl("http://www.nowcoder.com/101.png");
    user.setCreatTime(new Date());

    int rows = userMapper.insertUser(user);
    System.out.println(rows);
    System.out.println(user.getId());
  }

  @Test
  void updateStatus() {
    User user = userMapper.selectById(153);
    System.out.println(user);

    int rows = userMapper.updateStatus(153, 1);
    System.out.println(rows);
    // 通过id修改不会改变当前user对象
    System.out.println(user);

    System.out.println(userMapper.selectById(153));
  }

  @Test
  void updateHeader() {}

  @Test
  void updatePassword() {}
}
