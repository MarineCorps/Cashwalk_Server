package com.example.cashwalk.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponseDto {

    private Long id;              // 댓글 ID
    private Long userId;          // 작성자 ID
    private String content;       // 내용
    private LocalDateTime createdAt; // 작성 시각
}
