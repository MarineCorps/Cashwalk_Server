package com.example.cashwalk.service;

import com.example.cashwalk.dto.MonthlyStepsStatsDto;
import com.example.cashwalk.dto.StepsDto;
import com.example.cashwalk.dto.StepsStatsDto;
import com.example.cashwalk.entity.Steps;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.StepsRepository;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.cashwalk.dto.StepsTodayDto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 사용자의 걸음 수를 저장하고 포인트를 계산하는 서비스 클래스
 * - 하루에 하나의 기록만 저장됨
 * - 같은 날이면 기존 데이터 업데이트
 */
@Service
@RequiredArgsConstructor // 생성자 자동 생성 →
// StepsRepository, UserRepository 자동 주입
//final로 선언한 필드를 자동 생성자로 만들어줌
public class StepsService {

    private final StepsRepository stepsRepository;
    private final UserRepository userRepository;

    /**
     * 사용자의 걸음 수를 보고 받아 저장하거나 업데이트
     *
     * @param userId 사용자 ID (JWT에서 추출된 값)
     * @param request 사용자가 보낸 걸음 수 DTO
     */
    public void reportSteps(Long userId, StepsDto request) {
        // 1. 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 3. 오늘의 걸음 수 기록이 이미 있는지 확인
        Steps stepsRecord = stepsRepository.findByUserAndDate(user, today)
                .orElse(null);

        int newSteps = request.getSteps();

        if (stepsRecord == null) {
            // 4. 없으면 → 새로 만들기 (최초 보고)
            int points = newSteps / 100; // 100걸음 = 1포인트 규칙 (예시)

            Steps newRecord = Steps.builder()
                    .user(user)
                    .date(today)
                    .steps(newSteps)
                    .points(points)
                    .lastUpdated(today)
                    .build();

            stepsRepository.save(newRecord);

        } else {
            // 5. 있으면 → 걸음 수 증가분만큼 업데이트

            // 기존보다 줄어들었으면 무시
            if (newSteps <= stepsRecord.getSteps()) return;

            int stepDiff = newSteps - stepsRecord.getSteps();
            int pointDiff = stepDiff / 100;

            // 포인트 계산해서 추가
            stepsRecord.setSteps(newSteps);
            stepsRecord.setPoints(stepsRecord.getPoints() + pointDiff);
            stepsRecord.setLastUpdated(today);

            stepsRepository.save(stepsRecord);
        }
    }
    public StepsTodayDto getTodaySteps(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        LocalDate today = LocalDate.now();

        Steps steps = stepsRepository.findByUserAndDate(user, today)
                .orElse(null);

        if (steps == null) {
            // 오늘 기록이 없을 경우 0으로 반환
            return new StepsTodayDto(today.toString(), 0, 0);
        }

        return new StepsTodayDto(
                today.toString(),
                steps.getSteps(),
                steps.getPoints()
        );
    }
    public List<?> getStepStats(User user, String range) {
        LocalDate today = LocalDate.now();
        LocalDate startDate;

        switch (range.toLowerCase()) {
            case "daily" -> {
                startDate = today;
                List<Object[]> rawStats = stepsRepository.findStatsSinceDate(user.getId(), startDate);
                return rawStats.stream()
                        .map(obj -> new StepsStatsDto(
                                ((java.sql.Date) obj[0]).toLocalDate(),
                                ((Number) obj[1]).intValue()
                        ))
                        .collect(Collectors.toList());
            }
            case "weekly" -> {
                startDate = today.with(DayOfWeek.MONDAY); // ✅ 이번 주 월요일
                List<Object[]> rawStats = stepsRepository.findStatsSinceDate(user.getId(), startDate);
                return rawStats.stream()
                        .map(obj -> new StepsStatsDto(
                                ((java.sql.Date) obj[0]).toLocalDate(),
                                ((Number) obj[1]).intValue()
                        ))
                        .collect(Collectors.toList());
            }

            case "monthly" -> {
                startDate = today.minusMonths(5).withDayOfMonth(1); // 최근 6개월치 월별 통계
                List<Object[]> rawStats = stepsRepository.findMonthlyStats(user.getId(), startDate);
                return rawStats.stream()
                        .map(obj -> {
                            int year = ((Number) obj[0]).intValue();
                            int month = ((Number) obj[1]).intValue();
                            int steps = ((Number) obj[2]).intValue();
                            String monthStr = String.format("%04d-%02d", year, month);
                            return new MonthlyStepsStatsDto(monthStr, steps);
                        })
                        .collect(Collectors.toList());
            }
            default -> throw new IllegalArgumentException("range 파라미터는 daily, weekly, monthly 중 하나여야 합니다.");
        }

    }
    public List<?> getStepStatsByUserId(Long userId, String range) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
        return getStepStats(user, range);
    }




}
