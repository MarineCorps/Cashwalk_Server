package com.example.cashwalk.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {

    public enum MessageType {
        ENTER, TEXT, LEAVE, IMAGE, FILE, LUCKY_CASH
    }

    private String messageId;           // ✅ 메시지 고유 UUID (프론트에서 생성해서 전송)
    private MessageType type;           // 메시지 유형 (TEXT, IMAGE 등)
    private String roomId;              // 채팅방 ID
    private Long senderId;              // 보낸 사람 ID
    private String content;             // 메시지 내용
    private String fileUrl;             // 이미지나 파일 경로 (IMAGE, FILE일 경우)
    private LocalDateTime createdAt;    // ✅ 메시지 생성 시각

    // ✅ LUCKY_CASH 메시지 전용 필드
    private Boolean opened;             // 받았는지 여부
    private Boolean expired;            // 24시간 만료 여부
}
