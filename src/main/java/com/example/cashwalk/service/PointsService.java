package com.example.cashwalk.service;

import com.example.cashwalk.entity.Points;
import com.example.cashwalk.entity.PointsType;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.PointsRepository;
import com.example.cashwalk.repository.UserRepository;
import com.example.cashwalk.dto.PointsHistoryDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointsService {

    private final PointsRepository pointsRepository;
    private final UserRepository userRepository;

    // ✅ 포인트 내역 조회
    public List<PointsHistoryDto> getPointHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자없음"));
        List<Points> pointList = pointsRepository.findAllByUserOrderByCreatedAtDesc(user);

        return pointList.stream()
                .map(PointsHistoryDto::from)
                .collect(Collectors.toList());
    }

    // ✅ 사용자 총 포인트 반환
    public int getCurrentPoints(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자없음"));
        return user.getPoints();
    }

    // ✅ 관리자 수동 포인트 지급/차감 (PointsType = MANUAL)
    @Transactional
    public void addReward(User user, int amount, String description) {
        addReward(user, amount, PointsType.MANUAL, description);
    }

    // ✅ 모든 상황 대응: 보상 유형과 설명을 함께 처리
    @Transactional
    public void addReward(User user, int amount, PointsType type, String description) {
        // 1. 사용자 포인트 총액 갱신
        int newPoints = user.getPoints() + amount;
        user.setPoints(newPoints);

        // 2. 포인트 트랜잭션 생성
        Points points = new Points();
        points.setUser(user);
        points.setAmount(amount);
        points.setType(type); // ✅ 전달받은 포인트 유형
        points.setDescription(description);
        points.setCreatedAt(LocalDateTime.now());

        // 3. 저장
        pointsRepository.save(points);
        userRepository.save(user);
    }

    // ✅ 포인트 초기화 (내역도 함께 기록)
    @Transactional
    public void resetPoints(User user) {
        int currentPoints = user.getPoints();
        if (currentPoints == 0) return;

        Points points = new Points();
        points.setUser(user);
        points.setAmount(-currentPoints);
        points.setType(PointsType.RESET);
        points.setDescription("관리자에 의한 포인트 초기화");
        points.setCreatedAt(LocalDateTime.now());

        pointsRepository.save(points);

        user.setPoints(0);
        userRepository.save(user);
    }

    // ✅ 추천인/피추천인 보상 포인트 지급 전용 헬퍼 (옵션)
    public void addInviteReward(User user, int amount, String description) {
        addReward(user, amount, PointsType.INVITE_REWARD, description);
    }
}
