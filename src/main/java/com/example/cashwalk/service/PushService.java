package com.example.cashwalk.service;

import com.example.cashwalk.entity.DeviceToken;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Service // 비즈니스 로직 처리용 서비스 클래스
@RequiredArgsConstructor
@Slf4j
public class PushService {

    private final DeviceTokenRepository deviceTokenRepository;

    /**
     * FCM 디바이스 토큰을 등록합니다.
     * 중복된 유저-토큰 조합이 없다면 새로 저장합니다.
     */
    public void registerDeviceToken(User user, String token) {
        // 이미 해당 토큰이 DB에 존재하는지 먼저 확인
        DeviceToken existingToken = deviceTokenRepository.findByToken(token).orElse(null);

        if (existingToken == null) {
            // 토큰이 존재하지 않으면 새로 저장
            DeviceToken deviceToken = DeviceToken.builder()
                    .user(user)
                    .token(token)
                    .registeredAt(LocalDateTime.now())
                    .build();
            deviceTokenRepository.save(deviceToken);
        } else {
            // 토큰이 이미 존재하지만, 동일 유저가 아니라면 무시 (또는 덮어쓰기 로직)
            if (!existingToken.getUser().getId().equals(user.getId())) {
                // 다른 유저가 등록한 토큰이라면 무시 또는 로깅
                log.warn("❗ 이미 다른 유저에 의해 등록된 디바이스 토큰입니다. 무시됨. token={}, userId={}", token, user.getId());
            }
        }
    }

}
