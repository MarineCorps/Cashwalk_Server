package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor  // 기본 생성자 자동 생성
@AllArgsConstructor
public class LuckyCashHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 보낸 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    // ✅ 받은 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    // ✅ 채팅방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    // ✅ 연결된 메시지 (LUCKY_CASH)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private ChatMessage message;

    // ✅ 오늘 날짜 (보낸 날짜 기준) - 관리자 체크나 중복 전송용
    private LocalDate date;

    // ✅ 오픈 여부
    private boolean opened = false;

    // ✅ 만료 여부 (자정 스케줄러 기준)
    private boolean expired = false;

    // ✅ 생성 시각 (정확한 만료 시간 판단용)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
