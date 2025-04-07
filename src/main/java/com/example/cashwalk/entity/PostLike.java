package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


/*✅ 주요 포인트 설명
필드	설명
@UniqueConstraint	하나의 유저가 하나의 게시글에 중복된 좋아요/비추천 못 하게 함
Status enum	좋아요(LIKE), 비추천(DISLIKE)을 구분
@ManyToOne	유저, 게시글과 다대일 관계 설정
createdAt	생성 시간 자동 저장*/
@Entity
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="post_likes",uniqueConstraints = {
        @UniqueConstraint(columnNames={"user_id","post_id"})
})
public class PostLike {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    // 👍 좋아요 or 👎 비추천 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        LIKE,
        DISLIKE
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable=false)
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="post_id",nullable = false)
    private Post post;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


}
