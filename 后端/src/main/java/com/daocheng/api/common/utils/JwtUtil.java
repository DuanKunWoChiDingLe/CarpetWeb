package com.daocheng.api.common.utils;

import com.daocheng.api.common.constant.JwtConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 生成 Token
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtConstant.CLAIM_USER_ID, userId);
        claims.put(JwtConstant.CLAIM_USERNAME, username);
        claims.put(JwtConstant.CLAIM_ROLE, role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 Token
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 验证 Token 是否有效
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 从 Token 中获取用户 ID
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get(JwtConstant.CLAIM_USER_ID, Long.class);
    }

    // 从 Token 中获取用户名
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get(JwtConstant.CLAIM_USERNAME, String.class);
    }

    // 从 Token 中获取角色
    public String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get(JwtConstant.CLAIM_ROLE, String.class);
    }
}