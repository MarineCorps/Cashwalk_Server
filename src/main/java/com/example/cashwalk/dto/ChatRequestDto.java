package com.example.cashwalk.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequestDto {
    private Long receiverId; // 메시지를 받을 사용자 ID
    private String content;  // 전송할 메시지 내용
}

