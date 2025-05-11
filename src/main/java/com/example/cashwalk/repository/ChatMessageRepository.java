package com.example.cashwalk.repository;

import com.example.cashwalk.entity.ChatMessage;
import com.example.cashwalk.entity.ChatRoom;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 특정 채팅방의 메시지를 시간순으로 모두 조회
    List<ChatMessage> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);
    // ✅ 해당 채팅방에서 가장 마지막에 생성된 메시지 1개 조회
    Optional<ChatMessage> findTopByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);

    @Modifying //DB데이터를 변경하는 쿼리
    @Transactional //트랙잭션보장(오류가나면 복구)
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.chatRoom = :room AND m.sender.id <> :userId AND m.isRead = false")
    int markMessagesAsReadByOpponent(@Param("room") ChatRoom room, @Param("userId") Long userId);

    // ChatMessageRepository.java
    boolean existsBySenderAndTypeAndCreatedAtBetween(
            User sender,
            ChatMessage.MessageType type,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );

    Optional<ChatMessage> findByMessageId(String messageId);



}
