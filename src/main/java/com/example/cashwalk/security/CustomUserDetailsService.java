//Spring Security 사용자 정보
package com.example.cashwalk.security;

import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security가 사용자 정보를 가져올 때 사용하는 클래스
 * - JWT에 담긴 이메일로 사용자 정보를 조회하여 UserDetails로 변환
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Spring Security가 이메일(=username)로 사용자 조회 시 호출
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 이메일로 DB에서 사용자 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // User 객체를 Spring Security의 인증 객체로 변환
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // 이미 암호화된 비밀번호
                .roles("USER") // 기본 역할 설정 (추후 관리자로 확장 가능)
                .build();
    }
}
