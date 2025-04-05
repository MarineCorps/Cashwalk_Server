package com.example.cashwalk.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Builder

public class PostDetailResponseDto {
    private Long id;
    private String content;
    private String imageUrl;
    private Long userId;
    private LocalDateTime createdAt;

    private List<CommentResponseDto> comments;
}
