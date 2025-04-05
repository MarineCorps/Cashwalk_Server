//추천 코드 조회 및 적용 응답(추천인 ID, 보상상태)
package com.example.cashwalk.dto;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor //모든 필드를 파라미터로 받는 생성자 자동 생성
public class InviteDto {
    private String code; //추천 코드(regerrer의 초대코드)
    private boolean rewardReceived; //보상을 이미 받았는지 여부

    // 보상 메시지나 포인트 등 추가 정보가 필요하면 필드를 확장할 수 있음
}
