package com.example.cashwalk.controller;

import com.example.cashwalk.dto.PointsDto;
import com.example.cashwalk.dto.PointsHistoryDto;
import com.example.cashwalk.dto.StepsDto;
import com.example.cashwalk.entity.Park;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.ParkRepository;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 포인트 적립/차감 관련 API 컨트롤러
 * - 잔액 조회, 내역 조회 기능 포함
 */
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointsController {

    private final PointsService pointsService;
    private final ParkRepository parkRepository;
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
        int points = pointsService.getCurrentPoints(userDetails.getUserId());
        PointsDto response = new PointsDto(points); // 필요 시 DTO 변환
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

    @PostMapping("/walk")
    public ResponseEntity<Map<String,Object>> addNeighborhoodWalkPoint(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, Long> body
    ) {
        Long parkId = body.get("parkId");
        Park park = parkRepository.findById(parkId)
                .orElseThrow(() -> new RuntimeException("해당 공원이 없습니다."));

        String result = pointsService.addNeighborhoodWalkPoint(userDetails.getUser(), park);

        Map<String,Object> response = new HashMap<>();
        response.put("success", true);  // 성공 여부
        response.put("message", result); // 플러터랑 맞춤
        System.out.println("포인트 적립 응답: " + response);

        return ResponseEntity.ok(response);
    }



    @PostMapping("/step-reward")
    public ResponseEntity<String> addStepReward(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody StepsDto request
    ) {
        User user = userDetails.getUser();

        // 요청받은 걸음 수 → 포인트로 변환
        int requestedPoints = request.getSteps() / 100;

        String result = pointsService.addStepReward(user, requestedPoints);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/nwalk-today")
    public ResponseEntity<Set<Long>> getTodayNwalkParkIds(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Set<Long> result = pointsService.getTodayNwalkParkIds(userDetails.getUser());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/walk-record")
    public ResponseEntity<?> getWalkRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(pointsService.getWalkRecords(userDetails.getUserId(), year, month));
    }



}
