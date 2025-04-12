package com.example.cashwalk.config;

import com.example.cashwalk.security.JwtHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker // ✅ STOMP를 사용하는 WebSocket 활성화
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ✅ 클라이언트가 연결할 WebSocket 엔드포인트
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*")
                .addInterceptors(jwtHandshakeInterceptor) // JWT 인터셉터 등록
                .withSockJS(); // Flutter에서 SockJS 사용 가능하게 설정
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // ✅ 메시지를 구독할 때 사용하는 prefix (ex: /topic/message)
        registry.enableSimpleBroker("/topic");

        // ✅ 클라이언트가 서버로 메시지를 보낼 때 사용하는 prefix
        registry.setApplicationDestinationPrefixes("/app");
    }
}
