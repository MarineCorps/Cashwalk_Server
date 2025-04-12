package com.example.cashwalk.service;

import com.example.cashwalk.dto.BlockedUserDto;
import com.example.cashwalk.dto.UserDto;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.entity.UserBlock;
import com.example.cashwalk.repository.UserBlockRepository;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 클래스
 * DB에서 사용자 정보를 조회하고, 필요한 가공을 수행한다.
 */
@Service
@RequiredArgsConstructor // 생성자 주입 (final 필드 자동 생성자)
public class UserService {

    private final UserRepository userRepository;
    private final UserBlockRepository userBlockRepository;
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
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    /**
     * 다른 유저를 차단
     */
    @Transactional
    public void blockUser(Long blockerId, Long targetUserId) {
        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new IllegalArgumentException("차단자 정보를 찾을 수 없습니다."));
        User blocked = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("차단 대상 유저를 찾을 수 없습니다."));

        if (userBlockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
            throw new IllegalStateException("이미 차단한 유저입니다.");
        }

        UserBlock block = UserBlock.builder()
                .blocker(blocker)
                .blocked(blocked)
                .createdAt(LocalDateTime.now())
                .build();

        userBlockRepository.save(block);
    }

    /**
     * 차단 해제
     */
    @Transactional
    public void unblockUser(Long blockerId, Long targetUserId) {
        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new IllegalArgumentException("차단자 정보를 찾을 수 없습니다."));
        User blocked = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("차단 대상 유저를 찾을 수 없습니다."));

        userBlockRepository.deleteByBlockerAndBlocked(blocker, blocked);
    }

    /**
     * 내가 차단한 유저 목록 조회
     */
    @Transactional(readOnly = true)
    public List<BlockedUserDto> getBlockedUsers(Long blockerId) {
        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        List<UserBlock> blocks = userBlockRepository.findByBlocker(blocker);

        return blocks.stream()
                .map(block -> new BlockedUserDto(block.getBlocked().getId(), block.getBlocked().getNickname()))
                .collect(Collectors.toList());
    }

}
