package com.example.cashwalk.repository;

import com.example.cashwalk.entity.LuckyCashHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LuckyCashHistoryRepository extends JpaRepository<LuckyCashHistory, Long> {

    // ✅ 오늘 이미 보냈는지 확인 (하루 1회 제한용)
    boolean existsBySenderIdAndDate(Long senderId, LocalDate date);

    // ✅ 메시지 ID로 기록 조회 (복권 열기용)
    Optional<LuckyCashHistory> findByMessageId(Long messageId);

    // ✅ 자정 스케줄러용 - 아직 안 연 과거 기록들
    List<LuckyCashHistory> findByDateBeforeAndOpenedFalse(LocalDate today);
}
