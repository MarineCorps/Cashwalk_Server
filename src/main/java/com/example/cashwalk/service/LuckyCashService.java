package com.example.cashwalk.service;

import com.example.cashwalk.entity.LuckyCashHistory;
import com.example.cashwalk.repository.LuckyCashHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LuckyCashService {

    private final LuckyCashHistoryRepository luckyCashHistoryRepository;

    /**
     * ✅ 자정마다 실행되는 만료 처리기
     * - 열린 적 없는 메시지 중
     * - 오늘 이전 날짜의 행운캐시를 찾아서
     * - expired = true로 업데이트함
     */
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정 (00:00)
    @Transactional
    public void expireUnopenedLuckyCash() {
        LocalDate today = LocalDate.now();

        List<LuckyCashHistory> expiredTargets =
                luckyCashHistoryRepository.findByDateBeforeAndOpenedFalse(today);

        for (LuckyCashHistory history : expiredTargets) {
            history.setExpired(true);
        }

        luckyCashHistoryRepository.saveAll(expiredTargets);

        System.out.println("✅ [LuckyCash] 만료 처리 완료: " + expiredTargets.size() + "건");
    }

    /**
     * ✅ 특정 메시지 ID에 대한 opened / expired 상태 조회
     * (프론트에서 "열기 가능/완료/만료" 판단 용도로 사용 가능)
     */
    public StatusDto getLuckyCashStatus(Long messageId) {
        return luckyCashHistoryRepository.findByMessageId(messageId)
                .map(history -> {
                    boolean opened = history.isOpened();
                    boolean expired = history.getMessage().getCreatedAt()
                            .plusHours(24)
                            .isBefore(java.time.LocalDateTime.now());

                    return new StatusDto(opened, expired);
                })
                .orElseThrow(() -> new IllegalArgumentException("해당 행운캐시 기록이 없습니다."));
    }


    /**
     * ✅ 응답용 내부 DTO 클래스
     */
    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class StatusDto {
        private boolean opened;
        private boolean expired;
    }
}
