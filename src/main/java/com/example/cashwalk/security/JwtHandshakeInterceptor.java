package com.example.cashwalk.security;

import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        String token = null;

        // ✅ 헤더에서 Authorization: Bearer {token} 처리
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpReq = servletRequest.getServletRequest();
            String authHeader = httpReq.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else {
                // ✅ URL 파라미터에서 token=xxx 처리
                String uri = request.getURI().toString();
                if (uri.contains("token=")) {
                    token = uri.substring(uri.indexOf("token=") + 6);
                }
            }
        }

        // ✅ JWT 유효성 검사 + 사용자 조회
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getEmailFromToken(token);
            User user = userRepository.findByEmail(email).orElse(null);
            System.out.println("🔐 WebSocket Token: " + token);
            System.out.println("📧 Email from Token: " + email);
            System.out.println("👤 Found User: " + (user != null ? user.getNickname() : "null"));


            if (user != null) {
                attributes.put("userId", user.getId()); // ✅ 세션에 userId 저장
                return true;
            }
        }

        return false; // ❌ 인증 실패
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // 생략 가능
    }
}
