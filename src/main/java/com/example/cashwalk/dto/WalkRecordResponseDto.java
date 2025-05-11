package com.example.cashwalk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class WalkRecordResponseDto {
    private List<String> weeklyStamps;  // 이번 주 스탬프 요일 (예: ["월", "수"])
    private int weeklyCount;            // 이번 주 산책 일수
    private int monthlyCount;           // 이번 달 전체 산책 횟수
    private Map<String, List<Detail>> dailyRecords; // 일자별 산책 보상 상세 (예: "2025-04-14": [{시간, 공원명}])

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Detail {
        private String time;      // 적립 시각 (예: 09:30)
        private String parkName;  // 공원 이름
    }
}
