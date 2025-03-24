package com.example.cashwalk.controller;

import com.example.cashwalk.dto.AuthRequest;
import com.example.cashwalk.service.AuthService;
import com.example.cashwalk.dto.AuthIdTokenRequest;
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
     * 회원가입 API 엔드포인트
     * @param request 회원가입 요청 데이터 (이메일 & 비밀번호)
     * @return 회원가입 결과 메시지
     */
    //JSON데이터를 자바 객체로 변환
    //서비스 로직(AuthService)호출
    @PostMapping("/register")
    public String register(@RequestBody AuthRequest request) {
        return authService.register(request);
    }
    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }
    @PostMapping("/google")
    public String googleLogin(@RequestBody AuthIdTokenRequest request) {
        return authService.googleLogin(request.getIdToken());
    }
    @PostMapping("/kakao")
    public String kakaoLogin(@RequestBody String accessToken) {
        return authService.kakaoLogin(accessToken);
    }



}
