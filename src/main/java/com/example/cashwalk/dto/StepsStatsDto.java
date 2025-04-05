package com.example.cashwalk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

/**
 * ✅ 최근 7일 걸음 수 통계를 응답하기 위한 DTO
 */
@Data
@AllArgsConstructor
public class StepsStatsDto {
    private LocalDate date;
    private int steps;
}
