package com.example.cashwalk.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

//클라이언트로부터 전달받는 신고 요청을 담는 DTO
@Getter
@NoArgsConstructor //기본 생성자 자동 생성
public class ReportRequestDto {
    private Long targetId; //신고 대상 ID

    private String type; //신고 타입(댓글or게시글)

    private String reasonCode; //신고 사유

}
