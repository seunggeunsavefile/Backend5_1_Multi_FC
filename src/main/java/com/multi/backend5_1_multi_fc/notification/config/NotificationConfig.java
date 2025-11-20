package com.multi.backend5_1_multi_fc.notification.config;

import com.multi.backend5_1_multi_fc.security.JwtChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class NotificationConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtChannelInterceptor jwtChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic","/queue"); // topic 방법과 queue 방법의 prefix
        registry.setApplicationDestinationPrefixes("/app");             // client -> server 보낼 때 prefix (ex: /app/chat -> @MessageMapping("/chat") )
        registry.setUserDestinationPrefix("/user");                     // 특정 사용자에게 보내는 prefix
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/notification")       //WebSocket의 연결 기본 경로 (ex: ws://localhost:8080/notification )
                .setAllowedOriginPatterns("*")             //CORS 정책
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }
}