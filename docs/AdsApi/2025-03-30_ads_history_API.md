# ✅ 광고 보상 내역 조회 API 구현 (`/api/ads/history`)

### 🎯 목표
- 사용자가 광고를 시청했을 때, 받은 보상 내역을 날짜별로 확인할 수 있도록 `/api/ads/history` GET API를 구현한다.

---

## 📌 주요 작업 요약

| 작업 구분 | 파일명 | 설명 |
|----------|--------|------|
| DTO 생성 | `AdsHistoryDto.java` | 날짜 + 적립 포인트를 담은 응답용 DTO 생성 |
| Repository 확장 | `PointsRepository.java` | `AD_REWARD` 타입만 필터링해 정렬된 리스트 조회 메서드 추가 |
| Service 구현 | `AdsService.java` | 광고 보상 내역 조회 후 DTO 리스트로 변환 |
| Controller 구현 | `AdsController.java` | `/api/ads/history` GET API 구현 |

---

## 🧪 테스트 결과

### ✅ 응답 예시 (Postman)
```json
[
  {
    "date": "2025-03-30",
    "amount": 10
  }
]
```

✔️ JWT 인증 성공 시, 오늘 날짜의 광고 보상 내역이 잘 조회됨  
✔️ 중복 방지 후에도 보상 내역은 정상 출력됨

---

## 📅 정리: 오늘 한 일 요약
- 광고 보상 지급 API 구현 및 보상 내역 조회 API까지 마무리!
- `Points` 테이블의 `type`을 기준으로 필터링하고 DTO로 변환하는 일련의 흐름을 실습
- 보상 내역 확인이 가능하므로, 향후 출석 보상 등 다른 적립 내역도 확장 가능

🧠 **캐시워크 앱 구조에 맞춰 완전 동일하게 구현된 상태!**
