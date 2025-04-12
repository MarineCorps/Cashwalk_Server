package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 사용자가 다른 사용자를 차단한 기록을 저장하는 엔티티
 */
@Entity // 📌 JPA 엔티티(= 테이블)로 등록
@Getter // 📌 모든 필드에 대한 getter 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 📌 JPA 기본 생성자
@AllArgsConstructor
@Builder // 📌 빌더 패턴으로 객체 생성 가능
public class UserBlock {

    @Id // 📌 기본 키
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 📌 auto_increment
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 📌 차단한 유저
    @JoinColumn(name = "blocker_id")
    private User blocker;

    @ManyToOne(fetch = FetchType.LAZY) // 📌 차단당한 유저
    @JoinColumn(name = "blocked_id")
    private User blocked;

    private LocalDateTime createdAt; // 📌 차단한 시각
}
