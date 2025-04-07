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
                        //인증이 없어도 되는 기능
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/test/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/community/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"api/community/comments/post/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/community/comments/*/reactions").permitAll()

                        // 인증필요
                        .requestMatchers(HttpMethod.POST, "/api/community/posts/*/like").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/community/posts/*/dislike").authenticated()
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/ads/**").authenticated()
                        .requestMatchers("/api/store/**").authenticated()
                        .requestMatchers("/api/invite/**").authenticated()
                        .requestMatchers("/api/steps/stats").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/store/exchange").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/invite/apply").authenticated()
                        .requestMatchers("/api/points/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/community/comments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/community/comments/**").authenticated()
                        .requestMatchers("/api/events/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/community/comments/*/like").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/community/comments/*/dislike").authenticated()

                        //관리자기능
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        
                        

                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(customAuthenticationEntryPoint)  // ✅ 인증 실패 시 사용자 메시지 처리
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
