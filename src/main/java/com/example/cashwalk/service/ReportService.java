package com.example.cashwalk.service;

import com.example.cashwalk.dto.ReportRequestDto;
import com.example.cashwalk.entity.*;
import com.example.cashwalk.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 📌 신고 처리 비즈니스 로직 서비스
 */
@Service // 📌 비즈니스 로직을 담당하는 Service 계층임을 명시
@RequiredArgsConstructor // 📌 final 필드를 자동 주입하는 생성자 생성 (DI 편의성)
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /**
     * 📌 신고 저장 처리 메서드
     * @param reporterId 신고자 ID
     * @param request 신고 요청 DTO
     */
    @Transactional // 📌 DB 변경이 있는 로직이므로 트랜잭션 처리
    public void reportContent(Long reporterId, ReportRequestDto request) {
        // 🔍 신고자 조회
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("신고자 정보를 찾을 수 없습니다."));

        // 🔍 신고 타입 파싱
        ReportType type = ReportType.valueOf(request.getType()); // "POST" 또는 "COMMENT"
        ReportReasonCode reasonCode = ReportReasonCode.valueOf(request.getReasonCode()); // "ABUSE" 등

        Post post = null;
        Comment comment = null;

        // 🎯 신고 대상에 따라 post 또는 comment 조회
        if (type == ReportType.POST) {
            post = postRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new IllegalArgumentException("신고 대상 게시글이 존재하지 않습니다."));
        } else if (type == ReportType.COMMENT) {
            comment = commentRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new IllegalArgumentException("신고 대상 댓글이 존재하지 않습니다."));
        }

        // 🏗️ Report 객체 생성
        Report report = Report.builder()
                .reporter(reporter)
                .type(type)
                .reasonCode(reasonCode)
                .post(post)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();

        // 💾 저장
        reportRepository.save(report);
    }
}
