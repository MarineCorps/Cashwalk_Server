package com.example.cashwalk.repository;

import com.example.cashwalk.entity.ChatRoom;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByUser1AndUser2(User user1, User user2);
    Optional<ChatRoom> findByUser2AndUser1(User user2, User user1);
    List<ChatRoom> findAllByUser1OrUser2(User user1, User user2);

}
