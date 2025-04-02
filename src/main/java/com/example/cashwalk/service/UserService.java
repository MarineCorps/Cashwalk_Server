package com.example.cashwalk.service;

import com.example.cashwalk.dto.UserDto;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 클래스
 * DB에서 사용자 정보를 조회하고, 필요한 가공을 수행한다.
 */
@Service
@RequiredArgsConstructor // 생성자 주입 (final 필드 자동 생성자)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 사용자 ID로 사용자 정보를 조회한 후 DTO로 변환하여 반환
     * DTO란 데이터 전송 객체
     * 프로세스 간에 데이터를 전달하는 객체
     * @param userId 사용자 ID
     * @return UserDto 사용자 정보
     */
    public UserDto getUserInfo(Long userId) {
        // 사용자 ID로 DB에서 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다."));

        // Entity → DTO 변환 후 반환
        return UserDto.from(user);
    }
}
