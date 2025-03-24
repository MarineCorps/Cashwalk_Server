//로그인/회원가입 요청(이메일,비밀번호,닉네임 등)
package com.example.cashwalk.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 요청 데이터를 담는 DTO
 * 클라이언트에서 이메일과 비밀번호를 JSON으로 보낼 때 사용됨
 */

@Getter
@Setter
public class AuthRequest {
    private String email;
    private String password;
}
