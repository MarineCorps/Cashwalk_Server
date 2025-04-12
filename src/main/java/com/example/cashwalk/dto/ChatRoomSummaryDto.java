package com.example.cashwalk.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomSummaryDto {
    private Long roomId;               // 채팅방 ID
    private Long opponentId;          // 상대방 ID
    private String opponentNickname;  // 상대방 닉네임
    private String lastMessage;       // 마지막 메시지
    private LocalDateTime lastTime;   // 마지막 메시지 시간
}
