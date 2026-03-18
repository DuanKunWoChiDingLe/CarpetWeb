package com.daocheng.api.websocket.dto;

import lombok.Data;

@Data
public class NotificationMessage {
    private String type;      // 消息类型，如 "order", "system"
    private String content;   // 消息内容
    private Long targetUserId; // 目标用户ID（用于点对点）
    private String timestamp;
}