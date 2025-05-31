package com.example.cashwalk.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 러닝 기록 요약 카드용 응답 DTO
 */
@Getter
@Setter
public class RunningRecordResponseDto {

    private Long id; // 러닝 기록 고유 ID

    private double distance; // 러닝 거리

    private long duration; // 총 러닝 시간 (초)

    private double pace; // 평균 페이스 (min/km)

    private LocalDateTime startTime; // 시작 시간

    private LocalDateTime endTime; // 종료 시간
}
