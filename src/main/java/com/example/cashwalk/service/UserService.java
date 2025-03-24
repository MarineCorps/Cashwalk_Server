//사용자 정보 조회 및 프로필 수정, 추천 코드처리
package com.example.cashwalk.service;

import com.example.cashwalk.dto.UserDto;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * ✅ 현재 로그인한 사용자 정보 반환
     */
    public UserDto getMyInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        return new UserDto(user.getEmail(), user.getNickname(), user.getRole());
    }
}

