package com.daocheng.api.websocket.controller;

import com.daocheng.api.websocket.dto.NotificationMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 广播消息示例：客户端发送 /app/broadcast，服务端发送给所有订阅 /topic/broadcast 的客户端
    @MessageMapping("/broadcast")
    public void broadcast(NotificationMessage message) {
        messagingTemplate.convertAndSend("/topic/broadcast", message);
    }

    // 点对点消息：前端发送到 /app/private，服务端转发给指定用户
    @MessageMapping("/private")
    public void sendToUser(NotificationMessage message) {
        messagingTemplate.convertAndSendToUser(
                message.getTargetUserId().toString(),
                "/queue/notifications",
                message
        );
    }
}