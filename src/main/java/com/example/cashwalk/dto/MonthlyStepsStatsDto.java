package com.example.cashwalk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ✅ 월별 걸음 수 통계 응답용 DTO
 */
@Data
@AllArgsConstructor
public class MonthlyStepsStatsDto {
    private String month; // 예: "2025-04"
    private int steps;
}
