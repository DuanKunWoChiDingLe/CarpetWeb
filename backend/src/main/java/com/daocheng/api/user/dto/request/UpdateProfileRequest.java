package com.daocheng.api.user.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String nickname;
    private String avatar;
}