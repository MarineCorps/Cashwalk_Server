//사용자 정보 응답(닉네임,프로필사진,포인트 등)
package com.example.cashwalk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * ✅ 사용자 정보 응답 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private String email;
    private String nickname;
    private String role;
}

