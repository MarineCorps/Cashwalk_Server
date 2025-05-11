//로그인 응답(JWT 토큰, 사용자 정보)
package com.example.cashwalk.dto;
import lombok.*;
@Getter
@AllArgsConstructor
public class AuthResponse {
    private String jwt;
    private boolean isNewUser;
    private boolean firstLoginCompleted;
}
