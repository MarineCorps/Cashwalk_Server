package com.example.cashwalk.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 러닝 기록 상세 정보 응답 DTO
 */
@Getter
@Setter
public class RunningRecordDetailDto {

    private Long id; // 러닝 기록 고유 ID

    private double distance;

    private long duration;

    private double calories;

    private double pace;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer diaryLevel; // 1~7, null 허용 (러닝일기 난이도)

    private String diaryMemo;

    private List<LatLngDto> path; // 경로 리스트 (지도 생략해도 상관 없음)
}
