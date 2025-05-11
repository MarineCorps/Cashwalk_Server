package com.example.cashwalk.service;

import com.example.cashwalk.dto.WalkRecordResponseDto;
import com.example.cashwalk.entity.Park;
import com.example.cashwalk.entity.Points;
import com.example.cashwalk.entity.PointsType;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.PointsRepository;
import com.example.cashwalk.repository.UserRepository;
import com.example.cashwalk.dto.PointsHistoryDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        addReward(user, amount, PointsType.MANUAL, description,null);
    }

    // ✅ 모든 상황 대응: 보상 유형과 설명을 함께 처리
    @Transactional
    public void addReward(User user, int amount, PointsType type, String description, Park park) {
        int newPoints = user.getPoints() + amount;
        user.setPoints(newPoints);

        Points points = new Points();
        points.setUser(user);
        points.setAmount(amount);
        points.setType(type);
        points.setDescription(description);
        points.setCreatedAt(LocalDateTime.now());
        points.setPark(park);
        points.setParkName(park != null ? park.getName() : null); // 💡 parkName 필드 유지
        pointsRepository.save(points);
        userRepository.save(user);
    }

    @Transactional
    public void addReward(Long userId, int amount, PointsType type, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
        addReward(user, amount, type, description, null);
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
        addReward(user.getId(), amount, PointsType.INVITE_REWARD, description);
    }

    @Transactional
    public String addNeighborhoodWalkPoint(User user,Park park) {
        //오늘 하루의 시작-끝 계산
        LocalDateTime startOfDay=LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay=startOfDay.plusDays(1);


        //적립했는지 안했는지 확인
        boolean alreadyClaimed=pointsRepository.existsByUserAndTypeAndCreatedAtBetween(
                user,PointsType.NWALK,startOfDay,endOfDay
        );
        if(alreadyClaimed){
            return "이미 오늘 동네산책 포인트를 적립했습니다.";
        }

        addReward(user,10,PointsType.NWALK,"동네산책 포인트 적립",park);
        return "10포인트 적립 완료!";
    }

    // 산책 기록 조회 (NWALK)
    public WalkRecordResponseDto getWalkRecords(Long userId, int year, int month) {
        // 🔵 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        // 🔵 2. 조회 시작일, 종료일 설정 (ex: 2025-04-01 ~ 2025-05-01)
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);

        // 🔵 3. 사용자의 NWALK 포인트 내역을 필터링해서 가져옴
        List<Points> records = pointsRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(userId)
                        && p.getType() == PointsType.NWALK
                        && !p.getCreatedAt().isBefore(start)
                        && p.getCreatedAt().isBefore(end))
                .collect(Collectors.toList());

        // 🔵 4. 일자별로 그룹핑 (ex: 2025-04-02 -> 기록들 리스트)
        Map<String, List<WalkRecordResponseDto.Detail>> dailyMap = records.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCreatedAt().toLocalDate().toString(), // 날짜별 그룹
                        Collectors.mapping(
                                p -> WalkRecordResponseDto.Detail.builder()
                                        .time(p.getCreatedAt().toLocalTime().toString().substring(0, 5)) // HH:mm
                                        .parkName(p.getParkName())
                                        .build(),
                                Collectors.toList()
                        )
                ));

        // 🔵 6. 이번 주에 걸은 날짜들 요일로 변환 (\"월\", \"화\", \"수\" ...)
        LocalDate today = LocalDate.now();
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1); // 이번 주 월요일
        LocalDate sunday = monday.plusDays(6); // 이번 주 일요일

        // 이번 주 스탬프 요일 계산
        Set<String> weeklyStamps = records.stream()
                .filter(p -> {
                    LocalDate date = p.getCreatedAt().toLocalDate();
                    return !date.isBefore(monday) && !date.isAfter(sunday);
                })
                .map(p -> {
                    DayOfWeek dayOfWeek = p.getCreatedAt().getDayOfWeek();
                    return switch (dayOfWeek) {
                        case MONDAY -> "월";
                        case TUESDAY -> "화";
                        case WEDNESDAY -> "수";
                        case THURSDAY -> "목";
                        case FRIDAY -> "금";
                        case SATURDAY -> "토";
                        case SUNDAY -> "일";
                    };
                })
                .collect(Collectors.toSet());

        // 🔵 7. 최종 응답 데이터 조립
        return WalkRecordResponseDto.builder()
                .weeklyStamps(new ArrayList<>(weeklyStamps))
                .weeklyCount(weeklyStamps.size())
                .monthlyCount(dailyMap.size())
                .dailyRecords(dailyMap)
                .build();
    }

    @Transactional
    public String addStepReward(User user, int requested) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        int todayRewarded = pointsRepository.getTodayStepRewardSum(user, start, end);
        int available = Math.min(requested, 100 - todayRewarded);

        if (available <= 0) {
            return "오늘은 더 이상 적립할 수 없습니다 (100포인트 초과)";
        }

        addReward(user.getId(), available, PointsType.STEP_REWARD, "걸음수 보상");
        return available + "포인트 적립 완료!";
    }
    //하루가 지난 포인트 내역 삭제
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    @Transactional
    public void cleanOldStepRewards() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        pointsRepository.deleteOldStepRewards(todayStart);
        System.out.println("✅ 하루 지난 STEP_REWARD 기록 삭제 완료: " + todayStart);
    }

    public Set<Long> getTodayNwalkParkIds(User user) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return pointsRepository.findAllByUserAndTypeAndCreatedAtBetween(
                        user, PointsType.NWALK, start, end
                ).stream()
                .map(p -> p.getPark().getId())
                .collect(Collectors.toSet());
    }

    public int getTodayNwalkCount(User user) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return (int) pointsRepository.findAllByUserAndTypeAndCreatedAtBetween(
                user, PointsType.NWALK, start, end
        ).size();
    }



}
