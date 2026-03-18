package com.daocheng.api.user.controller;

import com.daocheng.api.common.dto.Result;
import com.daocheng.api.common.utils.JwtUtil;
import com.daocheng.api.user.dto.request.LoginRequest;
import com.daocheng.api.user.dto.request.RegisterRequest;
import com.daocheng.api.user.dto.request.UpdateProfileRequest;
import com.daocheng.api.user.dto.response.LoginResponse;
import com.daocheng.api.user.dto.response.UserInfoResponse;
import com.daocheng.api.user.entity.User;
import com.daocheng.api.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = userService.register(request);
        return Result.success(response);
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }

    @GetMapping("/me")
    public Result<UserInfoResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
        // 去除 Bearer 前缀
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error(401, "用户不存在");
        }
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setRole(user.getRole());
        response.setAvatar(user.getAvatar());
        return Result.success(response);
    }

    @PutMapping("/profile")
    public Result<UserInfoResponse> updateProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UpdateProfileRequest request) {
        // 去除 Bearer 前缀
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        User user = userService.updateProfile(userId, request.getNickname(), request.getAvatar());
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setRole(user.getRole());
        response.setAvatar(user.getAvatar());
        return Result.success(response);
    }
}