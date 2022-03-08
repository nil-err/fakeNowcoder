package com.han.fakeNowcoder.service;

import com.han.fakeNowcoder.dao.UserMapper;
import com.han.fakeNowcoder.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }
}
