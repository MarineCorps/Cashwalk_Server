package com.example.cashwalk.controller;

import com.example.cashwalk.dto.ReportRequestDto;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 신고 관련 요청을 처리하는 컨트롤러
 */
@RestController // 📌 REST API용 컨트롤러 (ResponseBody 자동 적용)
// 내부 메서드의 응답은 JSON형태로 변환됨
@RequestMapping("/api/report") // 📌 기본 URL 경로
@RequiredArgsConstructor // 📌 생성자 자동 주입
public class ReportController {

    private final ReportService reportService;

    /**
     * 게시글 또는 댓글 신고 API
     */
    @PostMapping // 📌 POST 요청 처리
    public ResponseEntity<String> reportContent(
            @AuthenticationPrincipal CustomUserDetails userDetails, // 📌 JWT 인증된 사용자 정보
            @RequestBody ReportRequestDto requestDto // 📌 클라이언트에서 전달한 신고 정보
    ) {
        Long reporterId = userDetails.getUserId(); // 사용자 ID 추출
        reportService.reportContent(reporterId, requestDto); // 서비스 호출
        return ResponseEntity.ok("신고가 정상적으로 접수되었습니다."); // 성공 응답
    }
}
