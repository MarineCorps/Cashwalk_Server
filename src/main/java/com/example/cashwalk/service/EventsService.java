package com.example.cashwalk.service;

import com.example.cashwalk.dto.AttendanceDto;
import com.example.cashwalk.entity.Attendance;
import com.example.cashwalk.entity.PointsType;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.cashwalk.service.PointsService;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EventsService {

    private final AttendanceRepository attendanceRepository;
    private final PointsService pointsService;

    /**
     * 오늘 출석 체크 처리
     */
    public AttendanceDto checkAttendance(User user) {
        LocalDate today = LocalDate.now();

        // 이미 오늘 출석했는지 확인
        attendanceRepository.findByUserAndDate(user, today).ifPresent(a -> {
            throw new IllegalStateException("이미 오늘 출석했습니다.");
        });

        // 출석 보상 포인트 (예: 10P)
        int reward = 10;

        // 포인트 적립 처리
        pointsService.addReward(user, reward, PointsType.ATTENDANCE,"출석보상지급");

        // 출석 기록 저장
        Attendance attendance = Attendance.builder()
                .user(user)
                .date(today)
                .reward(reward)
                .build();

        attendanceRepository.save(attendance);

        // DTO로 반환
        return AttendanceDto.from(attendance);
    }
}
