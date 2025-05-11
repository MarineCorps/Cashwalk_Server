package com.example.cashwalk.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증을 담당하는 유틸리티 클래스
 */
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long expirationMillis = 1000 * 60 * 60*24*7; // 1시간

    // 🔐 생성자에서 application.properties로부터 키를 주입받아 초기화
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        System.out.println("✅ JWT 시크릿 키: " + secretKey);
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 사용자 이메일을 기반으로 JWT 토큰 생성
     */
    public String createToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }
    /**
     * ✅ 사용자 이메일 + 역할 기반으로 JWT 토큰 생성 (오버로딩)
     */
    public String createToken(String email, String role, Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)          // 기존: 역할
                .claim("userId", userId)      // ✅ 추가: 사용자 ID
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }



    /**
     * JWT 토큰에서 사용자 이메일 추출
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 토큰 유효성 검사 (서명 및 만료일)
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
