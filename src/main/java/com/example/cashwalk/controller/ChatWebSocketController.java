package com.example.cashwalk.controller;

import com.example.cashwalk.dto.ChatMessageDto;
import com.example.cashwalk.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
/*WebSocket 전용
 – 실시간 채팅 메시지 송수신 처리 (@MessageMapping)*/

public class ChatWebSocketController {

    private final ChatService chatService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDto message) {
        chatService.handleMessage(message); // DB 저장 + 상대방에게 전송
    }
}
