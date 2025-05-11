package com.example.cashwalk.dto;

import com.example.cashwalk.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResponseDto implements ChatMessageBaseResponseDto {

    private String messageId;             // ✅ Flutter에서 messageId 필수
    private Long senderId;
    private String content;
    private String fileUrl;
    private ChatMessage.MessageType type;
    private LocalDateTime createdAt;

    public static ChatMessageResponseDto fromEntity(ChatMessage message) {
        System.out.println("💬 [ChatMessageResponseDto] 변환 시작");
        System.out.println("  ↪ messageId = " + message.getMessageId());
        System.out.println("  ↪ senderId  = " + message.getSender().getId());
        System.out.println("  ↪ content   = " + message.getContent());
        System.out.println("  ↪ type      = " + message.getType());
        System.out.println("  ↪ createdAt = " + message.getCreatedAt());

        return ChatMessageResponseDto.builder()
                .messageId(message.getMessageId())            // ✅ 반드시 포함
                .senderId(message.getSender().getId())
                .content(message.getContent())
                .fileUrl(message.getFileUrl())
                .type(message.getType())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
