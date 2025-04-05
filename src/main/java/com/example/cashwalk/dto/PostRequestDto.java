package com.example.cashwalk.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PostRequestDto {

    private String content; //게시글 텍스트 내용
    private Long userId; //작성자 ID(나중에 JWT에서 추출하도록 개선 가능)

}
