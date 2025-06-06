package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 게시글과 연관관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY) //(댓글:N 게시글:1)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 작성자와 연관관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String content;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CommentReaction> reactions = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    //하나의 부모댓글은 여러개의 대댓글을 가질 수있음 1:N
    //mappedBy="parent"라는 뜻은 parent필드가 연관관계의 주인이고, 외래키는 그쪽이 관리한다는뜻
    // cascade, orphanRemoval은 부모댓글을 삭제하면 자식 대댓글 다 삭제
    @OneToMany(mappedBy = "parent", cascade=CascadeType.REMOVE,orphanRemoval = true)
    private List<Comment> replies=new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
