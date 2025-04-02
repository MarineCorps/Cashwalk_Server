package com.example.cashwalk.service;

import com.example.cashwalk.dto.AdsDto;
import com.example.cashwalk.dto.AdsRewardHistoryDto;
import com.example.cashwalk.entity.Points;
import com.example.cashwalk.entity.PointsType;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.PointsRepository;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.cashwalk.dto.AdHistoryDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 🎥 광고 보상 처리 및 내역 조회를 담당하는 서비스 클래스
 */
@Service // 🧠 이 클래스는 비즈니스 로직을 담당하는 서비스임을 Spring에 알림
@RequiredArgsConstructor // 🔁 생성자를 자동 생성해 의존성 주입을 간결하게 처리
public class AdsService {

    private final UserRepository userRepository;
    private final PointsRepository pointsRepository;

    private final int REWARD_AMOUNT = 10; // 광고 1건당 적립 포인트

    /**
     * ✅ 광고 시청 보상 지급 메서드
     * - 같은 날 중복 지급은 금지됨
     * - 최초 시청 시 Points 엔티티 저장
     * - 이후 DTO로 응답 변환
     *
     * @param userId 인증된 사용자 ID (JWT 토큰에서 추출)
     * @return AdsDto (보상 포인트 정보)
     */
    public AdsDto rewardForAd(Long userId) {
        // 1. 사용자 조회 (예외 처리 포함)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 오늘 이미 보상 받았는지 체크
        if (pointsRepository.hasReceivedAdRewardToday(user)) {
            throw new IllegalStateException("오늘은 이미 광고 보상을 받았습니다.");
        }

        // 3. 포인트 엔티티 생성 및 저장
        Points reward = Points.builder()
                .user(user)
                .amount(REWARD_AMOUNT)
                .type(PointsType.AD_REWARD)
                .createdAt(LocalDateTime.now())
                .build();

        pointsRepository.save(reward);

        // 4. 응답 DTO 생성
        return new AdsDto(REWARD_AMOUNT);
    }

    /**
     * ✅ 광고 보상 내역 조회 (최신순 정렬)
     *
     * @param userId 사용자 ID
     * @return AdsRewardHistoryDto 리스트 (보상 날짜 및 포인트)
     */
    public List<AdsRewardHistoryDto> getAdRewardHistory(Long userId) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 광고 보상 내역만 조회 (날짜 내림차순 정렬)
        List<Points> adRewards = pointsRepository.findAdRewardsByUserOrderByCreatedAtDesc(user);

        // 3. DTO 변환
        return adRewards.stream()
                .map(AdsRewardHistoryDto::from) // → static 메서드 from(entity) 사용
                .collect(Collectors.toList());
    }

    public List<AdHistoryDto> getAdRewardDailySummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return pointsRepository.getAdRewardHistoryByUser(user, PointsType.AD_REWARD);
    }

}
