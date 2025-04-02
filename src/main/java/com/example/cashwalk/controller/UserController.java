package com.example.cashwalk.controller;

import com.example.cashwalk.dto.UserDto;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 API를 처리하는 컨트롤러
 * 주로 /api/users 경로로 들어오는 요청을 처리한다
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 현재 로그인한 사용자의 정보를 조회하는 API
     * JWT 인증 필터에서 사용자 정보가 `CustomUserDetails`로 주입됨
     *
     * @param userDetails 현재 로그인한 사용자의 JWT 기반 정보
     * @return UserDto 응답
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // 로그인한 사용자의 ID를 가져와서 서비스로 전달
        return ResponseEntity.ok(userService.getUserInfo(userDetails.getUserId()));
    }
}
