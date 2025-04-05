package com.example.cashwalk.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
//@Builder 사용 → 서비스에서 new 없이 .builder() 방식으로 쉽게 객체 생성 가능!
public class PostResponseDto {
    private Long id;  //게시글 고유 ID
    private String content; //게시글 내용
    private String imageUrl; //이미지 저장 경로
    private Long userId; //작성자 ID
    private LocalDateTime createdAt; //생성 시각
}
