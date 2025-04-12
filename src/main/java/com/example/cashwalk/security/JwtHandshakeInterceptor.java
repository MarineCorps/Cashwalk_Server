package com.example.cashwalk.security;

import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
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

        String uri = request.getURI().toString();
        String token = null;

        if (uri.contains("token=")) {
            token = uri.substring(uri.indexOf("token=") + 6);
        }

        if (token != null && jwtTokenProvider.validateToken(token)) {
            // ✅ 이메일 꺼내서 유저 조회
            String email = jwtTokenProvider.getEmailFromToken(token);
            User user = userRepository.findByEmail(email).orElse(null);

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
    }
}
