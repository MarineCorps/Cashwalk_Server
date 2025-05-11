package com.example.cashwalk.service;

import com.example.cashwalk.dto.GiftResultDto;
import com.example.cashwalk.entity.Gift;
import com.example.cashwalk.entity.Points;
import com.example.cashwalk.entity.PointsType;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.GiftRepository;
import com.example.cashwalk.repository.PointsRepository;
import com.example.cashwalk.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class GiftService {

    private final GiftRepository giftRepository;
    private final UserRepository userRepository;
    private final PointsRepository pointsRepository;

    private final Random random = new Random();

    @Transactional
    public GiftResultDto sendGift(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 보낸 사용자를 찾을 수 없습니다."));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 받는 사용자를 찾을 수 없습니다."));

        // 🔐 하루 1회 제한 체크
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        if (giftRepository.hasSentGiftToday(sender, receiver, todayStart)) {
            throw new IllegalStateException("❌ 오늘은 이미 선물을 보냈습니다.");
        }

        // 🎰 복권 보상 로직
        boolean isWinner = random.nextInt(100) < 10;
        int reward = isWinner ? random.nextInt(9001) + 1000 : random.nextInt(16) + 5;

        // 💰 1. receiver 포인트 증가
        receiver.setPoints(receiver.getPoints() + reward);
        userRepository.save(receiver);

        // 🧾 2. 포인트 적립 내역 저장
        Points points = Points.builder()
                .user(receiver)
                .amount(reward)
                .type(PointsType.GIFT_REWARD)
                .description("🎁 친구 선물 복권 보상")
                .createdAt(LocalDateTime.now())
                .build();
        pointsRepository.save(points);

        // 📝 3. 선물 기록 저장
        Gift gift = Gift.builder()
                .sender(sender)
                .receiver(receiver)
                .rewardAmount(reward)
                .isWinner(isWinner)
                .createdAt(LocalDateTime.now())
                .build();
        giftRepository.save(gift);

        // 📦 4. 응답 DTO 반환
        return GiftResultDto.builder()
                .rewardAmount(reward)
                .isWinner(isWinner)
                .message(isWinner ? "🎉 당첨! 축하합니다!" : "😅 비당첨이지만 캐시를 받았습니다.")
                .build();
    }
}
