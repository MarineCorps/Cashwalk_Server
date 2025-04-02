package com.example.cashwalk.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 💥 전역 예외 처리 클래스
 * 서비스 전반에서 발생하는 예외를 잡아 사용자에게 의미 있는 JSON 응답을 보냄
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 🧯 IllegalStateException 처리
     * - 예: 이미 보상을 받은 경우 등
     * - 응답: 400 Bad Request + JSON 메시지
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 💡 필요한 예외는 계속 추가 가능!
}
