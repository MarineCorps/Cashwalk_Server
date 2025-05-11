package com.example.cashwalk.service;

import com.example.cashwalk.dto.*;
import com.example.cashwalk.entity.*;
import com.example.cashwalk.repository.*;
import com.example.cashwalk.utils.PushNotificationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final DeviceTokenRepository deviceTokenRepository;
    private final PushNotificationUtil pushNotificationUtil;
    private final LuckyCashHistoryRepository luckyCashHistoryRepository;

    public void handleMessage(ChatMessageDto messageDto) {
        User sender = userRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Long receiverId = Long.parseLong(messageDto.getRoomId());
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("상대 사용자 없음"));

        Optional<ChatRoom> roomOptional = chatRoomRepository.findByUser1AndUser2(sender, receiver);
        if (roomOptional.isEmpty()) {
            roomOptional = chatRoomRepository.findByUser2AndUser1(receiver, sender);
        }

        ChatRoom room = roomOptional.orElseGet(() -> chatRoomRepository.save(
                ChatRoom.builder()
                        .user1(sender)
                        .user2(receiver)
                        .createdAt(LocalDateTime.now())
                        .build()
        ));

        // 💬 메시지 저장 (TEXT는 여기서 처리)
        ChatMessage saved = chatMessageRepository.save(
                ChatMessage.builder()
                        .chatRoom(room)
                        .sender(sender)
                        .content(messageDto.getContent())
                        .type(ChatMessage.MessageType.TEXT)
                        .fileUrl(messageDto.getFileUrl())
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .messageId(UUID.randomUUID().toString())  // ✅ 자동 생성
                        .build()
        );

        // 푸시 알림 발송
        List<DeviceToken> tokens = deviceTokenRepository.findByUser(receiver);
        for (DeviceToken token : tokens) {
            pushNotificationUtil.sendPushToToken(
                    token.getToken(),
                    "💬 새로운 메시지",
                    sender.getNickname() + ": " + messageDto.getContent()
            );
        }

        // WebSocket 전송
        ChatMessageResponseDto response = ChatMessageResponseDto.fromEntity(saved);
        messagingTemplate.convertAndSend("/topic/room." + receiver.getId(), response);
    }

    public List<ChatRoomSummaryDto> getChatRoomsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        List<ChatRoom> chatRooms = chatRoomRepository.findAllByUser1OrUser2(user, user);
        List<ChatRoomSummaryDto> summaries = new ArrayList<>();

        for (ChatRoom room : chatRooms) {
            if (chatRoomUserRepository.existsByUserAndChatRoomAndHiddenIsTrue(user, room)) continue;

            User opponent = room.getUser1().equals(user) ? room.getUser2() : room.getUser1();

            Optional<ChatMessage> lastMsgOpt = chatMessageRepository.findTopByChatRoomOrderByCreatedAtDesc(room);
            String lastMsg = lastMsgOpt.map(ChatMessage::getContent).orElse("대화 없음");
            LocalDateTime lastTime = lastMsgOpt.map(ChatMessage::getCreatedAt).orElse(null);

            summaries.add(ChatRoomSummaryDto.builder()
                    .roomId(room.getId())
                    .opponentId(opponent.getId())
                    .opponentNickname(opponent.getNickname())
                    .lastMessage(lastMsg)
                    .lastTime(lastTime)
                    .build());
        }

        return summaries;
    }

    public List<ChatMessageDto> getMessagesForRoom(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 채팅방이 존재하지 않습니다."));

        if (!room.getUser1().getId().equals(userId) && !room.getUser2().getId().equals(userId)) {
            throw new SecurityException("❌ 이 채팅방에 접근할 권한이 없습니다.");
        }

        List<ChatMessage> messages = chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(room);

        return messages.stream().map(msg -> {
            ChatMessageDto.ChatMessageDtoBuilder builder = ChatMessageDto.builder()
                    .messageId(msg.getMessageId())
                    .type(ChatMessageDto.MessageType.valueOf(msg.getType().name()))
                    .roomId(String.valueOf(room.getId()))
                    .senderId(msg.getSender().getId())
                    .content(msg.getContent())
                    .fileUrl(msg.getFileUrl())
                    .createdAt(msg.getCreatedAt());

            // ✅ LUCKY_CASH 메시지인 경우 opened, expired 정보 추가
            if (msg.getType() == ChatMessage.MessageType.LUCKY_CASH) {
                boolean opened = false;
                boolean expired = msg.getCreatedAt().plusHours(24).isBefore(LocalDateTime.now());

                Optional<LuckyCashHistory> historyOpt = luckyCashHistoryRepository.findByMessageId(msg.getId());
                if (historyOpt.isPresent()) {
                    LuckyCashHistory history = historyOpt.get();
                    opened = history.isOpened();
                }

                builder.opened(opened);
                builder.expired(expired);
            }

            return builder.build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public int markMessagesAsRead(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 채팅방이 존재하지 않습니다."));

        if (!room.getUser1().getId().equals(userId) && !room.getUser2().getId().equals(userId)) {
            throw new SecurityException("❌ 이 채팅방에 접근할 권한이 없습니다.");
        }

        return chatMessageRepository.markMessagesAsReadByOpponent(room, userId);
    }

    @Transactional
    public void hideChatRoom(Long userId, Long roomId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        Optional<ChatRoomUser> optionalRelation = chatRoomUserRepository.findByUserAndChatRoom(user, chatRoom);

        ChatRoomUser relation = optionalRelation.orElseGet(() -> ChatRoomUser.builder()
                .user(user)
                .chatRoom(chatRoom)
                .hidden(true)
                .build());

        relation.setHidden(true);
        chatRoomUserRepository.save(relation);
    }

    @Transactional
    public void unhideChatRoom(Long userId, Long roomId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        chatRoomUserRepository.findByUserAndChatRoom(user, chatRoom)
                .ifPresent(relation -> {
                    relation.setHidden(false);
                    chatRoomUserRepository.save(relation);
                });
    }

    public List<ChatRoomSummaryDto> getHiddenChatRoomsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        List<ChatRoomUser> hiddenRelations = chatRoomUserRepository.findByUserAndHiddenTrue(user);
        List<ChatRoomSummaryDto> summaries = new ArrayList<>();

        for (ChatRoomUser relation : hiddenRelations) {
            ChatRoom room = relation.getChatRoom();
            User opponent = room.getUser1().equals(user) ? room.getUser2() : room.getUser1();

            Optional<ChatMessage> lastMsgOpt = chatMessageRepository.findTopByChatRoomOrderByCreatedAtDesc(room);
            String lastMsg = lastMsgOpt.map(ChatMessage::getContent).orElse("대화 없음");
            LocalDateTime lastTime = lastMsgOpt.map(ChatMessage::getCreatedAt).orElse(null);

            summaries.add(ChatRoomSummaryDto.builder()
                    .roomId(room.getId())
                    .opponentId(opponent.getId())
                    .opponentNickname(opponent.getNickname())
                    .lastMessage(lastMsg)
                    .lastTime(lastTime)
                    .build());
        }

        return summaries;
    }

    /*
    public List<ChatMessageBaseResponseDto> getMessagesByRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        List<ChatMessage> messages = chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(room);

        return messages.stream()
                .map(message -> {
                    if (message.getType() == ChatMessage.MessageType.LUCKY_CASH) {
                        return luckyCashHistoryRepository.findByMessageId(message.getId())
                                .map(history -> (ChatMessageBaseResponseDto)
                                        LuckyCashMessageResponseDto.fromEntity(message, history.isOpened()))
                                .orElse(ChatMessageResponseDto.fromEntity(message));
                    } else {
                        return ChatMessageResponseDto.fromEntity(message);
                    }
                })
                .collect(Collectors.toList());
    }
    */
    @Transactional
    public Long getOrCreateChatRoom(Long myId, Long friendId) {
        User me = userRepository.findById(myId)
                .orElseThrow(() -> new IllegalArgumentException("내 정보 없음"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("상대 정보 없음"));

        Optional<ChatRoom> roomOpt = chatRoomRepository.findByUser1AndUser2(me, friend);
        if (roomOpt.isEmpty()) {
            roomOpt = chatRoomRepository.findByUser1AndUser2(friend, me);
        }

        ChatRoom room = roomOpt.orElseGet(() -> chatRoomRepository.save(
                ChatRoom.builder()
                        .user1(me)
                        .user2(friend)
                        .createdAt(LocalDateTime.now())
                        .build()
        ));

        return room.getId();
    }

    @Transactional
    public void sendLuckyCashMessage(ChatMessageDto messageDto, Long receiverId) {
        User sender = userRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("수신자 없음"));

        ChatRoom room = chatRoomRepository.findById(Long.parseLong(messageDto.getRoomId()))
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        boolean isAdmin = "ROLE_ADMIN".equals(sender.getRole());
        boolean alreadySent = luckyCashHistoryRepository.existsBySenderIdAndDate(sender.getId(), LocalDate.now());
        if (!isAdmin && alreadySent) {
            throw new IllegalStateException("오늘은 이미 행운 캐시를 보냈습니다.");
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .type(ChatMessage.MessageType.LUCKY_CASH)
                .content(messageDto.getContent())
                .fileUrl(null)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .messageId(messageDto.getMessageId())
                .build();
        chatMessageRepository.save(message);

        LuckyCashHistory history = new LuckyCashHistory();
        history.setSender(sender);
        history.setReceiver(receiver);
        history.setChatRoom(room);
        history.setMessage(message);
        history.setDate(LocalDate.now());
        luckyCashHistoryRepository.save(history);

        // ✅ WebSocket 응답도 ChatMessageDto로 보냄
        ChatMessageDto response = ChatMessageDto.builder()
                .messageId(message.getMessageId())
                .type(ChatMessageDto.MessageType.LUCKY_CASH)
                .roomId(String.valueOf(room.getId()))
                .senderId(sender.getId())
                .content(message.getContent())
                .fileUrl(null)
                .createdAt(message.getCreatedAt())
                .opened(false) // 보낸 직후이므로 열리지 않음
                .expired(false) // 보낸 직후이므로 만료 아님
                .build();

        messagingTemplate.convertAndSend("/topic/room." + receiverId, response);
    }

}
