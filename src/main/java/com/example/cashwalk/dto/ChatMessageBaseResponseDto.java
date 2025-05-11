package com.example.cashwalk.dto;

import java.time.LocalDateTime;

/**
 * ✅ 공통 응답 DTO 인터페이스
 * - 모든 채팅 메시지 응답 DTO는 이 인터페이스를 구현함
 * - 프론트에서 타입 상관없이 일괄적으로 처리 가능
 */
public interface ChatMessageBaseResponseDto {
    String getMessageId();         // 메시지 고유 ID
    Long getSenderId();            // 보낸 사람 ID
    String getContent();           // 메시지 본문
    String getFileUrl();           // 파일 URL (없을 경우 null)
    LocalDateTime getCreatedAt();  // 생성 시각
}
