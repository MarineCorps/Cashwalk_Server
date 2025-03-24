package com.example.cashwalk.security;

import com.example.cashwalk.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ✅ 매 요청마다 JWT 토큰을 검사하는 필터
 * - OncePerRequestFilter를 상속하여 요청 당 한 번 실행
 */
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    // ✅ Logger 설정
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // ✅ 인증이 필요 없는 경로는 필터 건너뜀
        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. 요청 헤더에서 Authorization 값 추출
        String header = request.getHeader("Authorization");

        if (header != null) {
            log.info("🔐 Authorization Header: {}", header);
        }

        // 2. "Bearer {토큰}" 형식인지 확인
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // "Bearer " 다음부터 잘라냄
            log.info("🧾 추출된 토큰: {}", token);

            // 3. 토큰 유효성 검사 및 사용자 이메일 추출
            if (jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                log.info("✅ 토큰 유효함 - 사용자 이메일: {}", email);

                // 4. 유저 정보 조회
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                log.info("👤 로드된 사용자 정보: {}", userDetails.getUsername());

                // 5. 인증 객체 생성 및 SecurityContext에 저장
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.info("🔓 SecurityContext에 인증 정보 저장 완료");
            } else {
                log.warn("❌ 토큰 유효성 검사 실패");
            }
        } else {
            log.warn("❌ 유효한 Authorization 헤더 없음 또는 형식이 잘못됨");
        }

        // 6. 다음 필터로 계속 진행
        filterChain.doFilter(request, response);
    }

}
