package com.example.cashwalk.utils;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

//PushService에서 호출해서 사용 가능함

@Slf4j // 로그 찍기용
@Component // 빈으로 등록해서 DI로 주입 가능하게 함
public class PushNotificationUtil {

    // 단일 토큰으로 푸시 알림 전송
    public void sendPushToToken(String targetToken, String title, String body) {
        try {
            // 알림 메시지 구성 (타이틀, 바디)
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // 메시지 구성: 어떤 디바이스에 어떤 알림을 보낼지
            Message message = Message.builder()
                    .setToken(targetToken)
                    .setNotification(notification)
                    .build();

            // Firebase로 푸시 전송
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("✅ 푸시 전송 성공 - response: {}", response);

        } catch (Exception e) {
            log.error("❌ 푸시 전송 실패", e);
        }
    }
}
