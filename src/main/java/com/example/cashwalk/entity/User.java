package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ✅ 사용자 정보 엔티티 (회원, 소셜 사용자 포함)
 */
@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본키

    @Column(nullable = false, unique = true)
    private String email; // 이메일 (로그인 ID)

    @Column
    private String password; // 비밀번호 (소셜 로그인은 빈 문자열 가능)

    @Column(nullable = false)
    private String nickname; // 사용자 닉네임

    @Column(nullable = false)
    private String role; // 권한 (ex. USER, ADMIN)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 가입일시

    @Column(nullable = false)
    private int points; // ✅ 사용자의 현재 보유 포인트

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.points = 0; // 가입 시 기본 포인트는 0으로 초기화
    }
}
