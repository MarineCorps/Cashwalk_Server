package com.example.cashwalk.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 🚨 에러 응답 DTO
 * 클라이언트에게 JSON 형식으로 에러 메시지를 전달
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String error;  // 에러 메시지
    private int code;      // HTTP 상태 코드
}
