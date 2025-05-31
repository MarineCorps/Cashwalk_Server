package com.example.cashwalk.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RunningRecordRequestDto {

    // 총 러닝 거리 (단위: km)
    private double distance;

    // 총 러닝 시간 (단위: 초) → Duration.ofSeconds(duration) 으로 변환 예정
    private long duration;

    // 총 소모 칼로리 (단위: kcal)
    private double calories;

    // 평균 페이스 (단위: 분/km)
    private double pace;

    // 러닝 경로 좌표 리스트 (경로 순서 포함)
    private List<LatLngDto> path;

    // 러닝 시작 시간 (ISO-8601 문자열 → LocalDateTime)
    private LocalDateTime startTime;

    // 러닝 종료 시간
    private LocalDateTime endTime;

    // 거리 기반 모드 여부 (true = 목표 거리 지정 러닝)
    private boolean isDistanceMode;

    // 자유 러닝 여부 (true = 제한 없는 자유 러닝)
    private boolean isUnlimited;

    // 러닝 일기 난이도 (1 = 매우 쉬움, 10 = 매우 힘듦) → null 허용
    private Integer diaryLevel;

    private String diaryMemo;
}
