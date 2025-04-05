package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity  // 이 클래스가 JPA의 엔티티임을 의미 (테이블과 매핑됨)
@Table(name = "posts")  // 테이블 이름 명시
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id  // 기본 키(게시물 ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 증가
    private Long id;

    @Column(nullable = false, length = 1000)  // 게시글 내용은 필수, 최대 1000자
    private String content;

    @Column(name = "image_url")  // 저장된 이미지의 경로 또는 URL
    private String imageUrl;

    @Column(name = "user_id", nullable = false)  // 작성자 ID (FK 매핑은 나중에 설정 가능)
    private Long userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();  // 저장 직전에 자동 생성일 등록
    }
}
