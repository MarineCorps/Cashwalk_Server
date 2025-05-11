package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    @Column(name = "invite_code", nullable = false, unique = true)
    private String inviteCode; // 사용자 고유 추천 코드

    @Column(name = "profile_image")
    private String profileImage; // 프로필 이미지 경로 또는 URL

    /**
     * ✅ 사용자 포인트 내역 리스트
     * - 사용자 삭제 시 관련 포인트도 같이 삭제됨
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Points> pointHistory = new ArrayList<>();

    @OneToMany(mappedBy = "referrer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invite> invites = new ArrayList<>();

    @Column
    private String gender; //성별

    @Column(name = "birth_date")
    private LocalDate birthDate; //생년월일

    @Column
    private String region; //지역

    @Column
    private Integer height;  //키

    @Column
    private Integer weight;  //몸무게

    @Column(name = "first_login_completed", nullable = false)
    private boolean firstLoginCompleted = false;

    // 🔥 여기에 추가

    @Column(name = "residence_address")
    private String residenceAddress; // 거주 지역

    @Column(name = "residence_certified_at")
    private LocalDate residenceCertifiedAt; // 거주 지역 인증 날짜

    @Column(name = "activity_address")
    private String activityAddress; // 활동 지역

    @Column(name = "activity_certified_at")
    private LocalDate activityCertifiedAt; // 활동 지역 인증 날짜



    /**
     * ✅ 사용자 생성 시 자동 초기화
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.totalPoints = 0;
        this.inviteCode = UUID.randomUUID().toString().substring(0, 8); // 8자리 추천코드 자동 생성
        this.profileImage = "";
    }
    // ✅ 기존 코드 호환을 위해 getPoints() 메서드 추가
    public int getPoints() {
        return this.totalPoints;
    }

    public void setPoints(int points) {
        this.totalPoints = points;
    }



}
