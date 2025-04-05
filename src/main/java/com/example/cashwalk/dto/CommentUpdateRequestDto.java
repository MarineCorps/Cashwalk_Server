package com.example.cashwalk.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentUpdateRequestDto {
    private String content;  // 수정할 댓글 내용
}
