package com.example.cashwalk.dto;

import com.example.cashwalk.entity.Points;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor

public class PointsHistoryDto {
    private LocalDate date; //날짜
    private int amount;  //포인트 금액
    private String type; //적립/ 사용 유형

    //Points 엔티티를 DTO로 변환하는 static 메서드
    public static PointsHistoryDto from(Points entity) {
        return new PointsHistoryDto(
                entity.getCreatedAt().toLocalDate(),  // 날짜만 추출
                entity.getAmount(),
                entity.getType().name() // ✅ enum → 문자열로 변환
        );
    }
}
