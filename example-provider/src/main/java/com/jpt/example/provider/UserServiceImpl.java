package com.jpt.example.provider;

import com.jpt.example.common.model.User;
import com.jpt.example.common.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
