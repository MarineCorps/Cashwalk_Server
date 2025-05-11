//사용자 디바이스 푸시 토큰 관리
package com.example.cashwalk.repository;

import com.example.cashwalk.entity.PushNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PushRepository extends JpaRepository<PushNotification, Long> {
    //Optional<PushNotification> findByUserId(Long userId);
}
