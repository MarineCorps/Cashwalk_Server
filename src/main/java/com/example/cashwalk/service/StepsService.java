package com.example.cashwalk.service;

import com.example.cashwalk.dto.MonthlyStepsStatsDto;
import com.example.cashwalk.dto.StepsDto;
import com.example.cashwalk.dto.StepsStatsDto;
import com.example.cashwalk.entity.Points;
import com.example.cashwalk.entity.PointsType;
import com.example.cashwalk.entity.Steps;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.PointsRepository;
import com.example.cashwalk.repository.StepsRepository;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.cashwalk.dto.StepsTodayDto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final PointsRepository pointsRepository;

    /**
     * 사용자의 걸음 수를 보고 받아 저장하거나 업데이트
     *
     * @param userId 사용자 ID (JWT에서 추출된 값)
     * @param request 사용자가 보낸 걸음 수 DTO
     */
    public void reportSteps(Long userId, StepsDto request) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        LocalDate today = LocalDate.now();
        Steps existingSteps = stepsRepository.findByUserAndDate(user, today).orElse(null);

        int newSteps = request.getSteps();

        if (existingSteps == null) {
            Steps newRecord = Steps.builder()
                    .user(user)
                    .date(today)
                    .stepCount(newSteps)
                    .points(0)
                    .lastUpdated(today)
                    .build();

            stepsRepository.save(newRecord);

        } else {
            // 🔁 기존 기록이 있을 경우 업데이트
            if (newSteps <= existingSteps.getStepCount()) return;

            int stepDiff = newSteps - existingSteps.getStepCount();
            int pointDiff = stepDiff / 100;

            // 🟡 기존 기록 업데이트
            existingSteps.setStepCount(newSteps);
            existingSteps.setPoints(existingSteps.getPoints() + pointDiff);
            existingSteps.setLastUpdated(today);
            stepsRepository.save(existingSteps);
        }
    }



    public StepsTodayDto getTodaySteps(Long userId) {
        System.out.println("📌 [StepsService] getTodaySteps 호출됨 - userId = " + userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("❌ [StepsService] 사용자 조회 실패"));

        System.out.println("✅ [StepsService] 사용자 조회 성공 - 닉네임: " + user.getNickname());

        LocalDate today = LocalDate.now();
        System.out.println("📅 [StepsService] 오늘 날짜: " + today);

        Steps steps = stepsRepository.findByUserAndDate(user, today)
                .orElse(null);

        if (steps == null) {
            System.out.println("⚠️ [StepsService] 오늘 걸음 수 기록 없음 - 0 반환");
            return new StepsTodayDto(today.toString(), 0, 0);
        }

        System.out.println("✅ [StepsService] 오늘 걸음 수 기록 조회 성공");
        System.out.println("👉 stepCount: " + steps.getStepCount() + ", points: " + steps.getPoints());

        return new StepsTodayDto(
                today.toString(),
                steps.getStepCount(),
                steps.getPoints()
        );
    }

    public List<?> getStepStats(User user, String range, LocalDate baseDate) {
        LocalDate startDate;

        switch (range.toLowerCase()) {
            case "daily" -> {
                startDate = baseDate; // ✅ baseDate 그대로 사용
                List<Object[]> rawStats = stepsRepository.findStatsSinceDate(user.getId(), startDate);
                return rawStats.stream()
                        .map(obj -> new StepsStatsDto(
                                ((java.sql.Date) obj[0]).toLocalDate(),
                                ((Number) obj[1]).intValue()
                        ))
                        .collect(Collectors.toList());
            }
            case "weekly" -> {
                startDate = baseDate.with(DayOfWeek.MONDAY); // ✅ baseDate 기준 이번 주 월요일
                List<Object[]> rawStats = stepsRepository.findStatsSinceDate(user.getId(), startDate);
                return rawStats.stream()
                        .map(obj -> new StepsStatsDto(
                                ((java.sql.Date) obj[0]).toLocalDate(),
                                ((Number) obj[1]).intValue()
                        ))
                        .collect(Collectors.toList());
            }
            case "monthly" -> {
                // 🔥 이번달 1일부터 말일까지
                LocalDate firstDayOfMonth = baseDate.withDayOfMonth(1);
                LocalDate lastDayOfMonth = baseDate.withDayOfMonth(baseDate.lengthOfMonth());

                List<Object[]> rawStats = stepsRepository.findStatsBetweenDates(user.getId(), firstDayOfMonth, lastDayOfMonth);
                System.out.println("✅ 월간 rawStats 결과: " + rawStats);
                return rawStats.stream()
                        .map(obj -> new StepsStatsDto(
                                (LocalDate) obj[0], // ✅ LocalDate로 캐스팅
                                ((Number) obj[1]).intValue()
                        ))
                        .collect(Collectors.toList());
            }

            default -> throw new IllegalArgumentException("range 파라미터는 daily, weekly, monthly 중 하나여야 합니다.");
        }
    }

    public List<?> getStepStatsByUserId(Long userId, String range, String dateStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        LocalDate baseDate = (dateStr != null)
                ? LocalDate.parse(dateStr) // 🔥 클라이언트가 보낸 날짜 사용
                : LocalDate.now();          // 🔥 없으면 서버 기준 오늘

        return getStepStats(user, range, baseDate);
    }


    public boolean claimPoint(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        LocalDate today = LocalDate.now();

        Steps steps = stepsRepository.findByUserAndDate(user, today)
                .orElse(null);

        if (steps == null) {
            // 오늘 걸음 수가 저장된 적 없음 → 포인트 수령 불가
            return false;
        }

        int stepsAvailablePoints = steps.getStepCount() / 100;
        int currentPoints = steps.getPoints();

        if (currentPoints >= 100 || currentPoints >= stepsAvailablePoints) {
            // ✅ 이미 오늘 수령 가능한 만큼 받음
            return false;
        }

        // ✅ 포인트 1 증가
        steps.setPoints(currentPoints + 1);
        steps.setLastUpdated(today);
        stepsRepository.save(steps);

        return true;
    }





}
