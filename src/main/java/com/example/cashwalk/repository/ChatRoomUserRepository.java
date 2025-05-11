package com.example.cashwalk.repository;

import com.example.cashwalk.entity.ChatRoom;
import com.example.cashwalk.entity.ChatRoomUser;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    // 해당 유저와 채팅방 관계가 이미 존재하는지 확인
    Optional<ChatRoomUser> findByUserAndChatRoom(User user, ChatRoom chatRoom);
    //현재 이 채팅방이 숨겨진 상태인지 확인
    boolean existsByUserAndChatRoomAndHiddenIsTrue(User user, ChatRoom chatRoom);
    //차단된 유저인지 아닌지 확인
    List<ChatRoomUser> findByUserAndHiddenTrue(User user);

}
