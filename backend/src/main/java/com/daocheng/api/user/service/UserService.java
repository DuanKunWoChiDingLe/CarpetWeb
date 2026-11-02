package com.daocheng.api.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daocheng.api.user.dto.request.LoginRequest;
import com.daocheng.api.user.dto.request.RegisterRequest;
import com.daocheng.api.user.dto.response.LoginResponse;
import com.daocheng.api.user.entity.User;

public interface UserService extends IService<User> {
    LoginResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    User getUserByUsername(String username);
    User updateProfile(Long userId, String nickname, String avatar);
}