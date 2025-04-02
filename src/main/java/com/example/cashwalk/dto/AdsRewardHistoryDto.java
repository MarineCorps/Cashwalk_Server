package com.example.cashwalk.dto;

import com.example.cashwalk.entity.Points;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Points → DTO 변환 전용
 * 사용자 광고 보상 내역을 보여줄 때 사용
 */
@Getter
@AllArgsConstructor
public class AdsRewardHistoryDto {
    private LocalDateTime date;
    private long amount;

    public static AdsRewardHistoryDto from(Points entity) {
        return new AdsRewardHistoryDto(
                entity.getCreatedAt(),
                entity.getAmount()
        );
    }
}
