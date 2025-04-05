//admincontroller의 @RequestBody로 사용됨
package com.example.cashwalk.dto;

import lombok.Data;

/**
 * 🔄 관리자 포인트 조정 요청 바디
 * 예시: { "userId": 1, "amount": 500, "description": "이벤트 보상 지급" }
 */
@Data // 🧠 Getter, Setter, toString 등을 자동 생성
public class ModifyPointsRequest {

    private Long userId;       // 대상 사용자 ID
    private int amount;        // 지급(+)/차감(-)할 포인트 양
    private String description; // 지급/차감 사유 설명
}
