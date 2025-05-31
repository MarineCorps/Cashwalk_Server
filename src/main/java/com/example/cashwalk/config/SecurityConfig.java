package com.example.cashwalk.config;

import com.example.cashwalk.security.CustomAuthenticationEntryPoint;
import com.example.cashwalk.security.JwtAuthenticationFilter;
import com.example.cashwalk.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        //  [공개 API - 인증 없이 접근 가능]
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/test/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/community/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/community/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/community/comments/**").permitAll()
                        // 👇 이건 로그인 없이도 허용
                        .requestMatchers(HttpMethod.GET, "/api/steps/today").permitAll()
                        .requestMatchers("/uploads/**").permitAll()

                        // 인증 필요함

                        // 걸음수 관련
                        .requestMatchers(HttpMethod.POST, "/api/steps/report").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/steps/claim").authenticated()
                        .requestMatchers("/api/steps/stats").authenticated()

                        //  [커뮤니티 게시글 - 인증 필요]
                        .requestMatchers(HttpMethod.POST, "/api/community/posts/*/bookmark").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/community/bookmarks/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/community/posts/*/like").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/community/posts/*/dislike").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/community/posts/*/detail").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/community/posts").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/community/posts/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/community/posts/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/community/myposts").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/community/mycomments").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/community/my-comments").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/community/my-commented-posts").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/community/my-replied-comments").authenticated()

                        //  [커뮤니티 댓글 - 인증 필요]
                        .requestMatchers(HttpMethod.POST, "/api/community/comments/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/community/comments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/community/comments/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/community/comments/*/like").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/community/comments/*/dislike").authenticated()
                        .requestMatchers("/api/favorite-boards").authenticated()
                        .requestMatchers("/api/favorites").authenticated()
                        //  [사용자 정보 - 인증 필요]
                        .requestMatchers("/api/users/me").authenticated()

                        //  [광고 / 포인트 / 초대 / 스토어 - 인증 필요]
                        .requestMatchers("/api/ads/**").authenticated()
                        .requestMatchers("/api/store/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/store/exchange").authenticated()
                        .requestMatchers("/api/invite/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/invite/apply").authenticated()
                        .requestMatchers("/api/points/**").authenticated()
                        .requestMatchers("/api/steps/stats").authenticated()

                        //  [이벤트 - 인증 필요]
                        .requestMatchers("/api/events/**").authenticated()
                        // 사용자 차단 기능
                        // 기존 설정 중 authorizeHttpRequests 내부에 아래 3줄 추가
                        .requestMatchers(HttpMethod.POST, "/api/users/block/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/block/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/blocked").authenticated()

                        //캐시톡
                        .requestMatchers("/api/chat/**").authenticated()
                        //친구한테 복권선물
                        .requestMatchers("/api/gift/**").authenticated()
                        .requestMatchers("/api/lucky-cash/**").authenticated()
                        .requestMatchers("api/running/**").authenticated()
                        //  [관리자 전용 API]
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        //친구목록 관련
                        .requestMatchers("/api/friends/**").authenticated()

                        .requestMatchers("/api/chat/**").authenticated()
                        .requestMatchers("/api/push/**").authenticated()
                        .requestMatchers("/api/certifications").authenticated()
                        // ✅ 동네산책 공원 조회 API 인증 필요 설정 추가
                        .requestMatchers(HttpMethod.POST, "/api/parks/nearby").authenticated()

                        // 신고기능
                        .requestMatchers(HttpMethod.POST, "/api/report").authenticated()
                        //  [기타 모든 요청은 인증 필요]
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
