package com.daocheng.api.user.dto.response;

import lombok.Data;

@Data
public class UserInfoResponse {
    private Long id;
    private String username;
    private String nickname;
    private String role;
    private String avatar;
}