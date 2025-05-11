package com.example.cashwalk.repository;

import com.example.cashwalk.entity.DeviceToken;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    // 특정 사용자에 등록된 모든 토큰 조회
    List<DeviceToken> findByUser(User user);

    // 중복 등록 방지를 위해 token으로 조회
    Optional<DeviceToken> findByToken(String token);

    // 특정 사용자 + 특정 토큰이 동시에 존재하는지 확인 (중복 방지용)
    Optional<DeviceToken> findByUserAndToken(User user, String token);
}
