package com.example.cashwalk.dto;

import com.example.cashwalk.entity.Comment;
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

    public static CommentResponseDto from(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

}
