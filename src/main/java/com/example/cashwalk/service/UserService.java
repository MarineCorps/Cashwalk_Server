package com.example.cashwalk.service;

import com.example.cashwalk.dto.BlockedUserDto;
import com.example.cashwalk.dto.UserDto;
import com.example.cashwalk.dto.UserProfileUpdateRequest;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.entity.UserBlock;
import com.example.cashwalk.repository.UserBlockRepository;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
     */
    public UserDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다."));
        return UserDto.from(user);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    /**
     * ✅ 사용자 정보(성별, 생일, 지역, 키, 몸무게) 업데이트 + 최초 로그인 처리
     */
    @Transactional
    public void updateUserInfo(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // ✅ 닉네임이 들어오면 업데이트
        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            user.setNickname(request.getNickname());
        }

        // ✅ 성별 업데이트
        if (request.getGender() != null && !request.getGender().isBlank()) {
            user.setGender(request.getGender());
        }

        // ✅ 생일 문자열이 들어오면 LocalDate로 파싱해서 저장
        if (request.getBirthDate() != null && !request.getBirthDate().isBlank()) {
            user.setBirthDate(LocalDate.parse(request.getBirthDate()));
        }

        // ✅ 지역
        if (request.getRegion() != null && !request.getRegion().isBlank()) {
            user.setRegion(request.getRegion());
        }

        // ✅ 키와 몸무게
        if (request.getHeight() != null) {
            user.setHeight(request.getHeight());
        }

        if (request.getWeight() != null) {
            user.setWeight(request.getWeight());
        }

        // ✅ 키와 몸무게가 모두 채워졌다면 firstLoginCompleted = true
        if (user.getHeight() != null && user.getWeight() != null) {
            user.setFirstLoginCompleted(true);
        }
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
