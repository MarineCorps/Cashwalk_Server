package com.example.cashwalk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

//스토어 아이템 교환 결과 응답 DTO
//교환된 아이템 정보와 남은 포인트를 포함

@Getter
@AllArgsConstructor //모든 필드를 받는 생성자 자동 생성
public class StoreItemExchangeResponse {
    private Long itemId; //교환된 아이템 ID
    private String itemName; //아이템 이름
    private int usedPoints; //차감된 포인트
    private int remainingPoints; //교환 후 남은 포인트
}
//@Getter -> getItemName(), getRemainingPoints() 자동 생성

//DTO는 일반적으로 불변 객체로 다루는 게 좋기 때문에,
// 생성자만 쓰고 setter는 생략