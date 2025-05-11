package com.example.cashwalk.controller;

import com.example.cashwalk.dto.AuthRequest;
import com.example.cashwalk.dto.AuthIdTokenRequest;
import com.example.cashwalk.dto.AuthResponse;
import com.example.cashwalk.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * ✅ 회원가입
     */
    @PostMapping("/register")
    public String register(@RequestBody AuthRequest request) {
        return authService.register(request);
    }

    /**
     * ✅ 일반 로그인 (이메일+비밀번호)
     */
    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    /**
     * ✅ 구글 로그인
     * Flutter에서 받은 idToken을 기반으로 로그인
     */
    @PostMapping("/google")
    public AuthResponse googleLogin(@RequestBody AuthIdTokenRequest request) {
        return authService.googleLogin(request.getIdToken());
    }

    /**
     * ✅ 카카오 로그인
     * Flutter에서 받은 accessToken을 기반으로 로그인
     */
    @PostMapping("/kakao")
    public AuthResponse kakaoLogin(@RequestBody String accessToken) {
        return authService.kakaoLogin(accessToken);
    }
}
