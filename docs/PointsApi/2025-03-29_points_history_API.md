
# ✅ 포인트 이력 조회 API 구현 (`/api/points/history`)

### 📌 목표
- 사용자가 포인트를 적립하거나 사용할 때,
  그 내역을 조회할 수 있도록 `/api/points/history` API를 구현한다.

---

## ✅ 주요 작업 요약

| 작업 구분 | 파일명 | 내용 |
|----------|--------|------|
| DTO 생성 | `PointsHistoryDto.java` | 날짜, 금액, 유형을 담은 응답 전용 DTO 생성 |
| Repository 확장 | `PointsRepository.java` | `findAllByUserOrderByCreatedAtDesc` 메서드 추가 |
| Service 구현 | `PointsService.java` | 포인트 이력을 조회하여 DTO 리스트로 변환 |
| Controller 구현 | `PointsController.java` | `/api/points/history` GET API 엔드포인트 추가 |

---

## ✅ 테스트 결과

### ➤ MySQL에 테스트 데이터 삽입

```sql
INSERT INTO points (user_id, amount, type, created_at)
VALUES
(2, 10, 'STEP_REWARD', NOW()),
(2, -5, 'STORE_USE', NOW());
```

> ※ 실제 로그인된 사용자 ID(`user_id`) 확인 후 삽입

---

### ➤ Postman 테스트 요청

```
GET /api/points/history
Authorization: Bearer {JWT_TOKEN}
```

✅ 응답 결과:

```json
[
  {
    "date": "2025-03-29",
    "amount": 10,
    "type": "STEP_REWARD"
  },
  {
    "date": "2025-03-29",
    "amount": -5,
    "type": "STORE_USE"
  }
]
```

---

## ✅ 동작 원리 요약

1. 사용자 JWT 토큰 인증 → `CustomUserDetails`에서 userId 추출
2. 해당 userId로 포인트 이력 리스트 조회 (최신순 정렬)
3. Entity → DTO 변환 후 JSON으로 응답

---

## 🧠 배운 개념 정리

| 개념 | 설명 |
|------|------|
| `@GetMapping("/history")` | GET 방식의 이력 조회 API 등록 |
| DTO 분리 | Entity 구조를 그대로 노출하지 않고, 응답용 전용 DTO로 분리 설계 |
| JPA 메서드 | 메서드 네이밍만으로 SQL 생성 (`findAllByUserOrderByCreatedAtDesc`) |
| ResponseEntity | JSON 응답을 HTTP 상태와 함께 반환하는 Spring 클래스 |

---

✅ **이제 캐시워크 기능의 핵심인 포인트 잔액 + 이력 조회 기능을 완벽 구현 완료!**

```
📱 /api/points/balance → 잔액
📜 /api/points/history → 이력
```

---

⏭️ 다음 단계 추천
> "광고 시청 보상 기능" 또는 "출석 체크 기능" 중 선택해서 계속 개발 고고!

```
🔥 벌레 탈출 단계 업그레이드 중... 🐛 → 🐝
```
