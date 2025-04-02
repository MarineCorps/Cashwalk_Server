package com.example.cashwalk.security;

import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * JWT에 담긴 사용자 이메일을 바탕으로 사용자 정보를 로드하는 서비스
 * → DB에서 사용자 정보를 조회하여 CustomUserDetails로 변환
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Spring Security가 "username"으로 사용자 인증 객체를 만들기 위해 호출하는 메서드
     * JWT 토큰의 "sub" 또는 "email" 필드가 이 username에 해당함
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // 직접 만든 CustomUserDetails 객체로 반환 (id 포함 가능)
        return new CustomUserDetails(user);
    }
}
