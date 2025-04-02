//포인트 내역 조회 응답(포인트 금액, 날짜, 유형)
package com.example.cashwalk.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

//사용자의 포인트 총합을 반환하기위한 응답 DTO
@Data //Getter +Setter +toString 자동 생성
@NoArgsConstructor //기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드 초기화하는 생성자 자동 완성
public class PointsDto {
    private int balance; //현재 총 보유 포인트
}
