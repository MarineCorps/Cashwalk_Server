package com.example.cashwalk.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GiftResultDto {
    private int rewardAmount;  // 지급된 캐시
    private boolean isWinner;  // 당첨 여부
    private String message;    // 응답 메시지 (예: 축하합니다!)
}
