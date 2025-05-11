package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "favorite_filter", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "board_type"}),
        @UniqueConstraint(columnNames = {"user_id", "post_category"})
})
public class FavoriteFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 사용자와의 관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ✅ 즐겨찾기한 게시판 타입 (nullable 허용)
    @Enumerated(EnumType.STRING)
    @Column(name = "board_type")
    private BoardType boardType;

    // ✅ 즐겨찾기한 게시글 카테고리 (nullable 허용)
    @Enumerated(EnumType.STRING)
    @Column(name = "post_category")
    private PostCategory postCategory;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // 💡 보조 메서드: 어떤 타입인지 판단하기 편하게
    public boolean isBoardTypeFavorite() {
        return boardType != null;
    }

    public boolean isPostCategoryFavorite() {
        return postCategory != null;
    }
}
