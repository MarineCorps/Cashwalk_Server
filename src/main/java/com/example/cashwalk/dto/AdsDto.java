//광고 시청 보상 응답(광고 ID,보상,포인트)
package com.example.cashwalk.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 광고 시청 완료 후 클라이언트에 보낼 응답 DTO
 * - 예: { "reward": 10 }
 */

@Data    // Getter, Setter, toString, equals, hashCode 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성 (JSON 역직렬화 시 필요)
@AllArgsConstructor // 모든 필드를 초기화하는 생성자 자동 생성
public class AdsDto {
    private int reward; // 지급된 포인트 (예: 광고 시청 보상으로 10포인트 지급)
}
