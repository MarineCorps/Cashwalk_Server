package com.example.cashwalk.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {

    public enum MessageType {
        ENTER, TALK, LEAVE
    }

    private MessageType type;   // ENTER, TALK, LEAVE 구분
    private String roomId;      // 채팅방 ID
    private Long senderId;      // 보낸 사람 ID
    private String content;     // 메시지 본문
}
