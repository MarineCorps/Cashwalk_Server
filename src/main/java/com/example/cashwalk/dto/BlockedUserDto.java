package com.example.cashwalk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 차단된 유저 목록 응답 DTO
 */
@Getter // 📌 모든 필드에 대한 getter 자동 생성
@AllArgsConstructor // 📌 모든 필드를 매개변수로 받는 생성자 생성
public class BlockedUserDto {

    private Long userId;      // 차단된 유저의 ID
    private String nickname;  // 차단된 유저의 닉네임
}
