package com.example.cashwalk.controller;

import java.util.*;
import com.example.cashwalk.dto.StepsDto;
import com.example.cashwalk.dto.StepsStatsDto;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.dto.StepsTodayDto;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.StepsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자의 걸음 수를 서버에 보고하는 API 컨트롤러
 * - 앱에서 실시간 또는 일정 주기로 호출됨
 */
@RestController //REST API 컨트롤러라는 의미(데이터 반환용)
@RequestMapping("/api/steps")
@RequiredArgsConstructor //생성자 생성
public class StepsController {

    private final StepsService stepsService;

    /**
     * 사용자의 현재 걸음 수를 서버에 보고하는 API
     * - JWT 인증된 사용자만 호출 가능
     * - DB에 오늘 날짜 기준으로 저장 or 업데이트
     *
     * @param userDetails 로그인한 사용자 정보 (JWT 기반)
     * @param request 걸음 수 DTO (JSON)
     * @return HTTP 200 OK
     */
    @PostMapping("/report")
    public ResponseEntity<String> reportSteps(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody StepsDto request
    ) {
        // 서비스 로직 호출
        stepsService.reportSteps(userDetails.getUserId(), request);

        return ResponseEntity.ok("걸음 수 저장 완료");
    }
    @GetMapping("/today")
    public ResponseEntity<StepsTodayDto> getTodaySteps(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        StepsTodayDto todaySteps = stepsService.getTodaySteps(userDetails.getUserId());
        return ResponseEntity.ok(todaySteps);
    }
    /**
     * ✅ 걸음 수 통계 조회 API (일간, 주간, 월간)
     * GET /api/steps/stats?range=daily|weekly|monthly
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStepStats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "range", defaultValue = "weekly") String range
    ) {
        try {
            Long userId = userDetails.getUserId(); // Long으로 userId 추출
            List<?> stats = stepsService.getStepStatsByUserId(userId, range); // 메서드 분기
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }




}
