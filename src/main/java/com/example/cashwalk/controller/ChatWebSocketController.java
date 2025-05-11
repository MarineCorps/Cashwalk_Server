package com.example.cashwalk.controller;

import com.example.cashwalk.dto.ChatMessageDto;
import com.example.cashwalk.dto.ChatMessageResponseDto;
import com.example.cashwalk.entity.ChatMessage;
import com.example.cashwalk.entity.ChatRoom;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.ChatMessageRepository;
import com.example.cashwalk.repository.ChatRoomRepository;
import com.example.cashwalk.repository.UserRepository;
import com.example.cashwalk.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
// WebSocket을 통한 실시간 채팅 처리 컨트롤러
public class ChatWebSocketController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDto messageDto) {
        Long senderId = messageDto.getSenderId();
        Long roomId = Long.parseLong(messageDto.getRoomId());

        // 사용자 조회
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("보낸 사람 없음"));

        // 채팅방 조회
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        // 상대방 추출
        User receiver = room.getUser1().equals(sender) ? room.getUser2() : room.getUser1();

        // 메시지 저장
        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .content(messageDto.getContent())
                .fileUrl(messageDto.getFileUrl())
                .type(ChatMessage.MessageType.valueOf(messageDto.getType().name()))
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .messageId(UUID.randomUUID().toString()) // ✅ UUID messageId 생성
                .build();

        chatMessageRepository.save(message);
        

        // DTO 변환 (프론트로 보낼 WebSocket 응답)
        ChatMessageResponseDto response = ChatMessageResponseDto.fromEntity(message);

        // 실시간 전송 (수신자 기준 채널)
        messagingTemplate.convertAndSend(
                "/topic/room." + receiver.getId(),
                response
        );
    }


}
