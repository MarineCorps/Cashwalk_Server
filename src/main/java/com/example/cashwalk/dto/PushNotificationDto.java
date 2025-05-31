//푸시 알림 정보 응답(제목, 내용, 발송시간)
package com.example.cashwalk.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushNotificationDto {
    private String token;
    private String title;
    private String body;
}
