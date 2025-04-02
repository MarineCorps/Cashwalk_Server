package com.example.cashwalk.repository;

import com.example.cashwalk.dto.AdHistoryDto;
import com.example.cashwalk.entity.Points;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.entity.PointsType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointsRepository extends JpaRepository<Points, Long> {

    /**
     * ✅ 특정 사용자의 총 포인트 잔액 계산
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Points p WHERE p.user = :user")
    int getTotalPointsByUser(@Param("user") User user);

    /**
     * ✅ 포인트 내역 전체 조회 (최신순)
     */
    List<Points> findAllByUserOrderByCreatedAtDesc(User user);

    /**
     * ✅ 광고 보상 내역 조회 (최신순)
     */
    @Query("SELECT p FROM Points p WHERE p.user = :user AND p.type = 'AD_REWARD' ORDER BY p.createdAt DESC")
    List<Points> findAdRewardsByUserOrderByCreatedAtDesc(@Param("user") User user);

    /**
     * ✅ 오늘 광고 보상을 받았는지 확인
     */
    @Query("SELECT COUNT(p) > 0 FROM Points p " +
            "WHERE p.user = :user AND p.type = 'AD_REWARD' " +
            "AND CAST(p.createdAt AS LocalDate) = CURRENT_DATE")
    boolean hasReceivedAdRewardToday(@Param("user") User user);

    /**
     * ✅ 날짜별 광고 보상 내역 (Hibernate 6 대응)
     */
    @Query("SELECT new com.example.cashwalk.dto.AdHistoryDto(" +
            "CAST(p.createdAt AS LocalDate), SUM(p.amount)) " +
            "FROM Points p " +
            "WHERE p.user = :user AND p.type = :type " +
            "GROUP BY CAST(p.createdAt AS LocalDate) " +
            "ORDER BY CAST(p.createdAt AS LocalDate) DESC")
    List<AdHistoryDto> getAdRewardHistoryByUser(
            @Param("user") User user,
            @Param("type") PointsType type
    );
}
