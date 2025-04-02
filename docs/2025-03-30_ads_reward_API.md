# ✅ 광고 보상 포인트 지급 API 구현 (`/api/ads/reward`)

### 🎯 목표
- 사용자가 광고를 시청한 뒤 보상을 받을 수 있도록, 광고 보상 포인트를 지급하는 API를 구현한다.

---

## 📌 주요 작업 요약

| 작업 구분 | 파일명 | 내용 |
|----------|--------|------|
| DTO 생성 | `AdsDto.java` | 보상 포인트 값을 담을 응답 전용 DTO |
| Service 구현 | `AdsService.java` | 광고 보상 포인트 지급 및 저장 로직 구현 |
| Controller 구현 | `AdsController.java` | `/api/ads/reward` POST API 구현 |
| Security 설정 | `SecurityConfig.java` | API 인증 설정 (경로 오타 수정: `"apie"` → `"api"`) |

---

## 🧪 테스트 결과

### 🔻 Postman 요청

```http
POST /api/ads/reward HTTP/1.1
Authorization: Bearer [JWT_TOKEN]
Content-Type: application/json

{
  "amount": 20,
  "type": "AD_REWARD"
}
```

### ✅ 응답

```json
{
  "reward": 10
}
```

---

## 🧠 문제 해결 기록

| 상황 | 내용 |
|------|------|
| 403 Forbidden 오류 | SecurityConfig.java에서 requestMatchers 경로 오타로 인해 인증 처리가 안 됨 |
| 원인 | `.requestMatchers("apie/ads/reward")` → `"api"` 오타 |
| 해결 | `.requestMatchers("/api/ads/reward").authenticated()` 로 수정 후 정상 작동 |

---

## 🧾 완료 시각

- 2025-03-30 13:24:22

