package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Gift;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;

public interface GiftRepository extends JpaRepository<Gift, Long> {

    // ✅ 오늘 날짜 기준으로 같은 사용자에게 이미 선물 보냈는지 확인
    @Query("SELECT COUNT(g) > 0 FROM Gift g " +
            "WHERE g.sender = :sender AND g.receiver = :receiver " +
            "AND g.createdAt >= :startOfDay")
    boolean hasSentGiftToday(User sender, User receiver, LocalDateTime startOfDay);
}
