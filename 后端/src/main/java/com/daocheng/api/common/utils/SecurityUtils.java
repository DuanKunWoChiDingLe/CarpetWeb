package com.daocheng.api.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("SecurityContext 中未找到 Authentication，用户未登录");
        }
        Object details = authentication.getDetails();
        if (details instanceof Long) {
            return (Long) details;
        }
        throw new IllegalStateException(
                "Authentication 的 details 不是 Long 类型，请检查 JwtAuthenticationFilter 是否正确设置了用户 ID");
    }
}