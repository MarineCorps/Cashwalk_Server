package com.example.cashwalk.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor //파라미터 없는 기본 생성자 자동 생성
@AllArgsConstructor //모든 필터를 파라미터로 받는 생성자 자동 생성
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne 
    /*ChatRoom 입장에서 user1은 다대일(N:1) 관계 
    (여러 채팅방 ↔ 하나의 유저)
    * */
    private User user1;

    @ManyToOne
    //마찬가지
    private User user2;

    private LocalDateTime createdAt;
}
