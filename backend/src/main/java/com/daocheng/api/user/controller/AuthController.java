package com.daocheng.api.user.controller;

import com.daocheng.api.common.dto.Result;
import com.daocheng.api.common.utils.CurrentUser;
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

    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(userService.register(request));
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @GetMapping("/me")
    public Result<UserInfoResponse> getCurrentUser() {
        User user = userService.getById(CurrentUser.getId());
        if (user == null) {
            return Result.error(401, "用户不存在");
        }
        return Result.success(toUserInfoResponse(user));
    }

    @PutMapping("/profile")
    public Result<UserInfoResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User user = userService.updateProfile(CurrentUser.getId(), request.getNickname(), request.getAvatar());
        return Result.success(toUserInfoResponse(user));
    }

    private UserInfoResponse toUserInfoResponse(User user) {
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setRole(user.getRole());
        response.setAvatar(user.getAvatar());
        return response;
    }
}
