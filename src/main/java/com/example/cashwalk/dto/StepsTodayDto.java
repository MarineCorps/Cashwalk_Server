package com.example.cashwalk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
//오늘의 걸음 수 및 포인트 정보를 프론트에 응답할 때 사용하는 DTO

@Data // Getter, Setter,
// toString, equals, hashCode 자동 생성
@AllArgsConstructor //모든 필드 받는 생성자 자동완성
public class StepsTodayDto {
    private String date; //오늘 날짜
    private int steps; //오늘까지 누적 걸음 수
    private int points; //오늘 적립된 포인트 수
}
