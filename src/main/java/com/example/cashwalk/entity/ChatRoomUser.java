package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_room_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"chat_room_id", "user_id"})
})   //동일한 유저-채팅방 쌍은 중복 저장되지 않도록 보장
public class ChatRoomUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 유저 ID  //누구의 설정인지 확인하기 위해 필요
    @ManyToOne
    @JoinColumn(name = "user_id") //외래키 사용
    private User user;

    // ✅ 채팅방 ID
    @ManyToOne    //어느 채팅방에 대한설정인지
    @JoinColumn(name = "chat_room_id")  //외래키 사용
    private ChatRoom chatRoom;

    // ✅ 숨김 여부 (true면 안 보이게)
    private boolean hidden;
}
