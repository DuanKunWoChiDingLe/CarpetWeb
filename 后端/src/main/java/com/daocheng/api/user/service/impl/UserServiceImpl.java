package com.daocheng.api.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daocheng.api.common.constant.UserRoleConstant;
import com.daocheng.api.common.enums.ResultCodeEnum;
import com.daocheng.api.common.exception.BusinessException;
import com.daocheng.api.common.security.CustomUserDetails;
import com.daocheng.api.common.utils.JwtUtil;
import com.daocheng.api.user.dto.request.LoginRequest;
import com.daocheng.api.user.dto.request.RegisterRequest;
import com.daocheng.api.user.dto.response.LoginResponse;
import com.daocheng.api.user.entity.User;
import com.daocheng.api.user.mapper.UserMapper;
import com.daocheng.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCodeEnum.USERNAME_EXIST);
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : UserRoleConstant.ROLE_CUSTOMER);
        user.setAvatar(null); // 默认无头像
        save(user);

        // 生成 token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        return buildLoginResponse(user, token);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 根据用户名查找用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = baseMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_EXIST);
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCodeEnum.PASSWORD_ERROR);
        }

        // 生成 token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        return buildLoginResponse(user, token);
    }

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return baseMapper.selectOne(wrapper);
    }

    private LoginResponse buildLoginResponse(User user, String token) {
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setRole(user.getRole());
        response.setAvatar(user.getAvatar());
        response.setToken(token);
        return response;
    }

    @Override
    @Transactional
    public User updateProfile(Long userId, String nickname, String avatar) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_EXIST);
        }
        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (avatar != null) {
            user.setAvatar(avatar);
        }
        updateById(user);
        return user;
    }

    // 实现 UserDetailsService 接口，供 Spring Security 使用
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        // 暂时不分配角色，后续如需角色权限可添加 GrantedAuthority
        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>(),
                user.getId()
        );
    }
}