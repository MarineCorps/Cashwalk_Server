package com.example.cashwalk.controller;

import com.example.cashwalk.service.PushService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController // REST API용 컨트롤러
@RequestMapping("/api/push") // /api/push 경로로 들어오는 요청 처리
@RequiredArgsConstructor
public class PushController {

    private final PushService pushService;

    @PostMapping("/register") // 디바이스 토큰 등록용 POST API
    public ResponseEntity<String> registerToken(
            @RequestBody DeviceTokenRequest request, // 요청 body에 담긴 token 받기
            @AuthenticationPrincipal(expression = "user") com.example.cashwalk.entity.User user // JWT 인증된 사용자 정보 받기
    ) {
        // 서비스 로직 호출 → 토큰 저장
        pushService.registerDeviceToken(user, request.getToken());
        log.info("🔁 푸시 등록 요청: userId={}, token={}", user.getId(), request.getToken());;
        return ResponseEntity.ok("디바이스 토큰이 등록되었습니다.");
    }

    // 내부 요청 DTO
    public static class DeviceTokenRequest {
        @NotBlank
        private String token;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
