package com.daocheng.api.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册端点，供前端连接，允许跨域，支持 SockJS
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 应用前缀：前端发送消息的目标地址前缀
        registry.setApplicationDestinationPrefixes("/app");
        // 简单代理：广播消息前缀
        registry.enableSimpleBroker("/topic", "/queue");
        // 点对点消息前缀（用户专用）
        registry.setUserDestinationPrefix("/user");
    }
}