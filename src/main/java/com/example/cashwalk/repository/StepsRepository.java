package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Steps;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Steps 엔티티에 대한 DB 조회/저장을 처리하는 인터페이스
 * - Spring Data JPA가 자동으로 구현체를 만들어줌
 */
@Repository
public interface StepsRepository extends JpaRepository<Steps, Long> {

    /**
     * 특정 사용자와 날짜에 해당하는 걸음 수 기록을 조회
     * - user_id + date = unique 조합을 기준으로 조회
     */
    Optional<Steps> findByUserAndDate(User user, LocalDate date);

    // ✅ 일/주간 통계 조회용 쿼리
    @Query(
            value = """
        SELECT DATE(s.date) AS stat_date, SUM(s.steps) AS total_steps
        FROM steps s
        WHERE s.user_id = :userId
          AND s.date >= :startDate
        GROUP BY stat_date
        ORDER BY stat_date ASC
        """,
            nativeQuery = true
    )
    List<Object[]> findStatsSinceDate(@Param("userId") Long userId,
                                      @Param("startDate") LocalDate startDate);

    @Query("""
        SELECT s.date, SUM(s.stepCount)
        FROM Steps s
        WHERE s.user.id = :userId AND s.date BETWEEN :startDate AND :endDate
        GROUP BY s.date
        ORDER BY s.date
        """)
    List<Object[]> findStatsBetweenDates(Long userId, LocalDate startDate, LocalDate endDate);

}
