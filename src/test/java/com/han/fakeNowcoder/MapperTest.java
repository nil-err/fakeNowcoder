package com.han.fakeNowcoder;


import com.han.fakeNowcoder.dao.UserMapper;
import com.han.fakeNowcoder.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = FakeNowcoderApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(150);
        System.out.println(user);
        user = userMapper.selectByName("liubei");
        System.out.println(user);
        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("Lihua");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("AAA@han.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreatTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser() {
        User user = userMapper.selectById(150);
        System.out.println(user);

        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);
        // 通过id修改不会改变当前user对象
        System.out.println(user);

        System.out.println(userMapper.selectById(150));
    }

}
