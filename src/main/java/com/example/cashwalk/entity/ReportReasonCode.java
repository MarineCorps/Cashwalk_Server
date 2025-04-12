package com.example.cashwalk.entity;

/**
 *신고 사유 항목 Enum
 *프론트에서 선택 가능한 고정된 리스트로 구성
 */
public enum ReportReasonCode {
    ABUSE,             // 욕설/비하 발언
    SEXUAL,            // 음란성
    PROMOTION,         // 홍보성 콘텐츠 및 도배글
    PRIVACY,           // 개인정보 노출
    DEFAMATION,        // 특정인 비방
    ETC                // 기타
}
