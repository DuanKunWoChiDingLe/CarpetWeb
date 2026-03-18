package com.daocheng.api.order.service;

import com.daocheng.api.websocket.dto.NotificationMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class OrderNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public OrderNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyOrderStatusChanged(Long userId, Long orderId, String newStatus) {
        NotificationMessage msg = new NotificationMessage();
        msg.setType("order");
        msg.setContent("订单 " + orderId + " 状态已更新为 " + newStatus);
        msg.setTargetUserId(userId);
        msg.setTimestamp(LocalDateTime.now().toString());

        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", msg);
    }
}