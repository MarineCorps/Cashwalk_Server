package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;
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

    @Column(name = "total_points", nullable = false)
    private int totalPoints; // 현재 총 보유 포인트 (기본값 0)

    /**
     * ✅ 사용자 포인트 내역 리스트
     * - 사용자 삭제 시 관련 포인트도 같이 삭제됨
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Points> pointHistory = new ArrayList<>();

    @OneToMany(mappedBy = "referrer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invite> invites = new ArrayList<>();


    /**
     * ✅ 사용자 생성 시 자동 초기화
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.totalPoints = 0;
    }
    // ✅ 기존 코드 호환을 위해 getPoints() 메서드 추가
    public int getPoints() {
        return this.totalPoints;
    }

    public void setPoints(int points) {
        this.totalPoints = points;
    }

}
