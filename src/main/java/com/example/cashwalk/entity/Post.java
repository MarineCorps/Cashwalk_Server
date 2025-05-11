package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    // ✅ 사용자 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType boardType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostCategory postCategory = PostCategory.GENERAL;

    @Column(nullable = false)
    private int views = 0;

    @Column(nullable = false)
    private int bookmarkCount = 0;

    @Column(name = "comment_count",columnDefinition = "int default 0",nullable = false)
    private int commentCount=0;


    private LocalDateTime createdAt;

    // ✅ 댓글 연관관계
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // ✅ 좋아요 연관관계
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    // ✅ 북마크 연관관계
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();
}
