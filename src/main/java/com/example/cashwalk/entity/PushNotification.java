package com.example.cashwalk.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;

@Entity  // ✅ 이게 있어야 JPA에서 관리됨
@Getter
@Setter
public class PushNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String body;
    private String deviceToken;
    private String createdAt;

    // 필드 추가 시 여기에 정의
}
