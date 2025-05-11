package com.example.cashwalk.dto;

import com.example.cashwalk.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LuckyCashMessageResponseDto implements ChatMessageBaseResponseDto {

    private String messageId;
    private Long senderId;
    private String content;
    private String fileUrl;
    private LocalDateTime createdAt;

    // 🎯 LuckyCash 전용 필드
    private boolean opened;
    private boolean expired;

    // ✅ senderId, content 등 공통 필드 + opened / expired 상태 수동 주입
    public static LuckyCashMessageResponseDto fromEntity(ChatMessage message, boolean opened, boolean expired) {
        System.out.println("🍀 [LuckyCashMessageResponseDto] messageId=" + message.getMessageId() +
                ", senderId=" + message.getSender().getId() +
                ", opened=" + opened + ", expired=" + expired +
                ", content=" + message.getContent());

        return LuckyCashMessageResponseDto.builder()
                .messageId(message.getMessageId())
                .senderId(message.getSender().getId())
                .content(message.getContent())
                .fileUrl(message.getFileUrl())
                .createdAt(message.getCreatedAt())
                .opened(opened)
                .expired(expired)
                .build();
    }
}
