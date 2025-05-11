package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor  // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 자동 생성
public class Gift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id") // 선물 보낸 사용자
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id") // 선물 받은 사용자
    private User receiver;

    private int rewardAmount; // 지급된 캐시

    private boolean isWinner; // 당첨 여부

    private LocalDateTime createdAt; // 선물 보낸 시간
}
