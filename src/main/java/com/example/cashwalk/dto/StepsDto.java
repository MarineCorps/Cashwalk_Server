//걸음 수 보고 및 조회 응답(날짜, 걸음 수, 적립 포인트)
package com.example.cashwalk.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//롬복은 자바에서 반복적으로 써야하는 (Getter/Setter/생성자)등을 자동으로
//만들어주는 도구
//Getter는 값을 다시 뱉어내는(읽어오는), Setter는 값을 받아 멤버변수에 저장

/**
 * DTO = Data Transfer Object, 즉 "데이터 전달용 객체"
 * 사용자가 걸음 수를 서버에 보고할 때 사용하는 요청 DTO
 * - 예: { "steps": 4532 }JSON형태로 변환
 */

@Getter
@Setter
@NoArgsConstructor  //매개변수가 하나도 없는 기본 생성자르 자동으로 만들어줌
public class StepsDto {
    private int steps;
}
