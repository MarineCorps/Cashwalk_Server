package com.example.cashwalk.controller;

import com.example.cashwalk.dto.PointsDto;
import com.example.cashwalk.dto.PointsHistoryDto;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 포인트 적립/차감 관련 API 컨트롤러
 * - 잔액 조회, 내역 조회 기능 포함
 */
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointsController {

    private final PointsService pointsService;

    /**
     * 현재 로그인한 사용자의 포인트 잔액을 조회하는 API
     *
     * @param userDetails JWT 인증된 사용자 정보
     * @return 현재 보유 포인트
     */
    @GetMapping("/balance")
    public ResponseEntity<PointsDto> getPointBalance(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PointsDto response = pointsService.getPointBalance(userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    /**
     * 포인트 사용/적립 이력 전체 조회 API
     * @return 최신순 정렬된 포인트 이력 리스트
     */
    @GetMapping("/history")
    public ResponseEntity<List<PointsHistoryDto>> getPointHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<PointsHistoryDto> history = pointsService.getPointHistory(userDetails.getUserId());
        return ResponseEntity.ok(history);
    }
}
