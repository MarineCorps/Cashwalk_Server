package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Attendance;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * 출석 기록을 조회/저장하는 JPA 리포지토리
 */
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 특정 사용자와 날짜에 대한 출석 여부 확인
    Optional<Attendance> findByUserAndDate(User user, LocalDate date);
  //Optional은 존재할 수 도있고, 안 할 수도있는 결과를 감싸서 반환함
}
