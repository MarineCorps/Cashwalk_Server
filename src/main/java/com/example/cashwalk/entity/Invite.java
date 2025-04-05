package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invite")
@Getter
@Setter
@NoArgsConstructor
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 추천한 사용자 (추천인)
    @ManyToOne
    @JoinColumn(name = "referrer_id", nullable = false)
    private User referrer;

    // 추천을 받은 사용자 (피추천인)
    @OneToOne
    @JoinColumn(name = "invitee_id", unique = true)
    private User invitee;

    // 추천 코드 (referrer가 가진 코드)
    @Column(name = "invite_code", nullable = false, unique = true)
    private String inviteCode;

    // 추천 적용 일시
    @Column(name = "applied_at")
    private java.time.LocalDateTime appliedAt;
}
