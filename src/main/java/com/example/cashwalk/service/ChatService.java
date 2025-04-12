/*
사용자가 메시지를 보내면
채팅방이 있는지 확인 → 없으면 생성
메시지를 저장
해당 채팅방에 접속 중인 상대방에게 전송 */
package com.example.cashwalk.service;

import com.example.cashwalk.dto.ChatMessageDto;
import com.example.cashwalk.dto.ChatRoomSummaryDto;
import com.example.cashwalk.entity.ChatMessage;
import com.example.cashwalk.entity.ChatRoom;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.ChatMessageRepository;
import com.example.cashwalk.repository.ChatRoomRepository;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void handleMessage(ChatMessageDto messageDto) {
        // 보낸 사람 조회
        User sender = userRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        // 채팅방 ID → user1, user2를 구분해서 저장하는 경우엔 채팅방 탐색이 다름 (예시용으로 roomId=상대 userId 로 가정)
        Long receiverId = Long.parseLong(messageDto.getRoomId());
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("상대 사용자 없음"));

        // 기존 채팅방이 있는지 확인 (양방향 모두 체크)
        Optional<ChatRoom> roomOptional = chatRoomRepository.findByUser1AndUser2(sender, receiver);
        if (roomOptional.isEmpty()) {
            roomOptional = chatRoomRepository.findByUser2AndUser1(receiver, sender);
        }

        // 🔍 채팅방이 이미 있는 경우 사용, 없으면 새로 생성
        ChatRoom room = roomOptional.orElseGet(() -> {
            // 🆕 새로운 채팅방 생성 (보낸 사람, 받는 사람)
            ChatRoom newRoom = ChatRoom.builder()
                    .user1(sender)
                    .user2(receiver)
                    .createdAt(LocalDateTime.now())
                    .build();
            return chatRoomRepository.save(newRoom); // DB에 저장 후 반환
        });

// 💬 메시지 저장 (누가, 어떤 방에서, 어떤 내용으로 보냈는지)
        ChatMessage saved = chatMessageRepository.save(
                ChatMessage.builder()
                        .chatRoom(room) // 어떤 채팅방에 속하는 메시지인지
                        .sender(sender) // 메시지 보낸 사용자
                        .content(messageDto.getContent()) // 실제 메시지 텍스트
                        .createdAt(LocalDateTime.now()) // 보낸 시각
                        .isRead(false) // 기본값은 '읽지 않음'
                        .build()
        );


        // 상대방에게 메시지 전송 (구독 주소: /topic/room.{receiverId})
        messagingTemplate.convertAndSend("/topic/room." + receiver.getId(), messageDto);
    }
    //채팅방 목록 조회
    public List<ChatRoomSummaryDto> getChatRoomsForUser(Long userId) {
        // 🔐 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        // 📦 내가 포함된 모든 채팅방 조회 (user1 또는 user2)
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByUser1OrUser2(user, user);

        List<ChatRoomSummaryDto> summaries = new ArrayList<>();

        for (ChatRoom room : chatRooms) {
            // 👤 상대방 정보 구하기
            User opponent = room.getUser1().equals(user) ? room.getUser2() : room.getUser1();

            // 💬 해당 채팅방의 마지막 메시지 조회
            Optional<ChatMessage> lastMsgOpt = chatMessageRepository
                    .findTopByChatRoomOrderByCreatedAtDesc(room);

            // 🔎 마지막 메시지 내용과 시간 추출
            String lastMsg = lastMsgOpt.map(ChatMessage::getContent).orElse("대화 없음");
            LocalDateTime lastTime = lastMsgOpt.map(ChatMessage::getCreatedAt).orElse(null);

            // 📤 클라이언트로 보낼 요약 DTO 생성
            summaries.add(ChatRoomSummaryDto.builder()
                    .roomId(room.getId())
                    .opponentId(opponent.getId())
                    .opponentNickname(opponent.getNickname()) // nickname 필드 있다고 가정
                    .lastMessage(lastMsg)
                    .lastTime(lastTime)
                    .build()
            );
        }

        return summaries;
    }
    //채팅방 들어갔을때 메세지뜨는 로직
    public List<ChatMessageDto> getMessagesForRoom(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 채팅방이 존재하지 않습니다."));

        // 🔐 접근 권한 체크: user1 또는 user2 중 하나여야 함
        if (!room.getUser1().getId().equals(userId) && !room.getUser2().getId().equals(userId)) {
            throw new SecurityException("❌ 이 채팅방에 접근할 권한이 없습니다.");
        }

        // 📥 해당 방의 모든 메시지 조회 (오래된 순)
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(room);

        // DTO 변환
        return messages.stream().map(msg ->
                ChatMessageDto.builder()
                        .type(ChatMessageDto.MessageType.TALK) // 메시지 타입은 기본적으로 TALK
                        .roomId(String.valueOf(room.getId()))
                        .senderId(msg.getSender().getId())
                        .content(msg.getContent())
                        .build()
        ).toList();
    }

    @Transactional
    public int markMessagesAsRead(Long roomId, Long userId) {
        // 1. 채팅방 존재 확인
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 채팅방이 존재하지 않습니다."));

        // 2. 권한 확인
        if (!room.getUser1().getId().equals(userId) && !room.getUser2().getId().equals(userId)) {
            throw new SecurityException("❌ 이 채팅방에 접근할 권한이 없습니다.");
        }

        // 3. 메시지 읽음 처리
        return chatMessageRepository.markMessagesAsReadByOpponent(room, userId);
    }



}

