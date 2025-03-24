package com.example.cashwalk.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 비밀번호 암호화 및 검증을 위한 유틸리티 클래스
 * BCrypt 해싱을 사용하여 보안 강화
 */
public class PasswordUtil {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    //BCryptPasswordEncoder를 사용하여 비밀번호를 암호화
    // 비밀번호는 한번 암호화되면 다시 원래 값으로 복원할수없음(단방향 해싱)
    //같은 비밀번호여도 매번 다르게 됨
    /**
     * 비밀번호를 암호화하는 메서드
     * @param rawPassword 사용자가 입력한 원본 비밀번호
     * @return 암호화된 비밀번호
     */
    public static String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
        
    }

    /**
     * 사용자가 입력한 비밀번호가 저장된 암호화 비밀번호와 일치하는지 확인하는 메서드
     * @param rawPassword 사용자가 입력한 비밀번호
     * @param encodedPassword 데이터베이스에 저장된 암호화된 비밀번호
     * @return 일치하면 true, 아니면 false
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

