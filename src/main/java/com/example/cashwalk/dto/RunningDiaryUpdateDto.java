package com.example.cashwalk.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 러닝 일기 수정 요청 DTO
 */
@Getter
@Setter
public class RunningDiaryUpdateDto {

    // 난이도 (1~7), null 허용
    private Integer diaryLevel;

    // 한줄 메모, null 또는 빈 문자열 허용
    private String diaryMemo;
}
