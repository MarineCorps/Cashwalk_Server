package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 푸시 토큰을 가진 사용자
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // FCM 디바이스 토큰 (클라이언트에서 수신한 값)
    @Column(nullable = false, unique = true)
    private String token;

    // 토큰이 등록된 시각
    private LocalDateTime registeredAt;
}
