package com.example.cashwalk.controller;

import com.example.cashwalk.dto.BlockedUserDto;
import com.example.cashwalk.dto.UserDto;
import com.example.cashwalk.dto.UserProfileUpdateRequest;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 다른 유저 차단
     */
    //차단요청 처리
    @PostMapping("/block/{targetUserId}")
    public ResponseEntity<String> blockUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long targetUserId
    ) {
        Long blockerId = userDetails.getUserId();
        userService.blockUser(blockerId, targetUserId);
        return ResponseEntity.ok("해당 유저를 차단했습니다.");
    }

    /**
     * 차단 해제
     */
    @DeleteMapping("/block/{targetUserId}")
    public ResponseEntity<String> unblockUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long targetUserId
    ) {
        Long blockerId = userDetails.getUserId();
        userService.unblockUser(blockerId, targetUserId);
        return ResponseEntity.ok("차단을 해제했습니다.");
    }

    /**
     * 내가 차단한 유저 목록 조회
     */
    @GetMapping("/blocked")
    public ResponseEntity<List<BlockedUserDto>> getBlockedUsers(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long blockerId = userDetails.getUserId();
        List<BlockedUserDto> blockedUsers = userService.getBlockedUsers(blockerId);
        return ResponseEntity.ok(blockedUsers);
    }

    /**
     * ✅ 사용자 정보 입력/수정 (성별, 생일, 지역, 키, 몸무게)
     */
    @PatchMapping("/info")
    public ResponseEntity<?> updateUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserProfileUpdateRequest request
    ) {
        userService.updateUserInfo(userDetails.getUser().getId(), request);
        return ResponseEntity.ok("사용자 정보가 업데이트되었습니다.");
    }
}
