package com.jpt.example.common.service;

import com.jpt.example.common.model.User;

public interface UserService {
    User getUser(User user);

    default short getNumber() {
        return 1;
    }
}
