package com.example.cashwalk.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Builder

public class PostDetailResponseDto {
    private Long id;
    private String title;
    private String nickname;
    private String content;
    private String imageUrl;
    private Long userId;
    private LocalDateTime createdAt;

    private int likeCount;
    private int commentCount;
    private int views;

    private boolean likedByMe;  // 로그인한 유저가 좋아요 눌렀는지

    private List<CommentResponseDto> comments;
}
