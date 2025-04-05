# ✅ 친구 초대 보상 포인트 지급 기능 API 개발 기록

---

## 📆 날짜

2025년 4월 4일 (금)

---

## 🎯 구현 목표

- 추천 코드 적용 시 추천인(referrer)과 피추천인(invitee) 모두에게 보상 포인트를 지급
- 포인트 지급 내역을 `points` 테이블에 `INVITE_REWARD` 유형으로 기록
- 사용자 총 포인트 잔액 업데이트

---

## 📁 디렉토리별 역할 요약

| 디렉토리 | 역할 |
|----------|------|
| `entity` | `Points` 엔티티에 `description` 필드 추가, 포인트 적립 유형(Enum) 사용 |
| `service` | `PointsService`에 `addInviteReward()` 메서드 구현 |
| `InviteService` | 추천 코드 적용 성공 시 보상 지급 호출 |
| `repository` | 포인트 트랜잭션 저장 처리 |

---

## 🧾 수정/생성한 파일 목록

| 파일명 | 위치 | 설명 |
|--------|------|------|
| `Points.java` | `entity` | `description` 필드 추가, `PointsType` Enum 사용 |
| `PointsType.java` | `entity` | `INVITE_REWARD` Enum 항목 추가 |
| `PointsService.java` | `service` | `addInviteReward()` 메서드로 보상 지급 기능 구현 |
| `InviteService.java` | `service` | 코드 적용 시 추천인/피추천인에게 각각 포인트 지급 로직 구현 |

---

## 🔁 전체 실행 흐름

1. 사용자가 추천 코드를 입력하여 `/api/invite/apply?code=xxxx` 호출
2. 추천인(referrer)과 피추천인(invitee) 정보 추출
3. 각각에게 100포인트씩 지급
4. `Points` 엔티티에 `INVITE_REWARD` 유형으로 트랜잭션 2건 저장
5. 사용자 포인트 총액 증가

---

## 📌 주요 코드

### 📍 PointsService.java

```java
public void addInviteReward(User user, int amount, String description) {
    int updatePoints = user.getPoints() + amount;
    user.setPoints(updatePoints);

    Points points = new Points();
    points.setUser(user);
    points.setAmount(amount);
    points.setType(PointsType.INVITE_REWARD);
    points.setDescription(description);
    points.setCreatedAt(LocalDateTime.now());

    pointsRepository.save(points);
    userRepository.save(user);
}
```

---

### 📍 InviteService.java 내부 적용

```java
pointsService.addInviteReward(referrer, 100, "친구 초대 보상 지급 (추천인)");
pointsService.addInviteReward(invitee, 100, "친구 초대 보상 지급 (피추천인)");
```

---

## 🧪 테스트 결과 (Postman)

| 요청 | 결과 |
|------|------|
| `/api/invite/apply?code=xxxx` | ✅ 200 OK |
| `/api/points/history` | ✅ `INVITE_REWARD` 유형으로 2건 저장됨 |
| `/api/points/balance` | ✅ 총 포인트 정상 증가 반영됨 |

```json
[
  {
    "date": "2025-04-04",
    "amount": 3000,
    "type": "INVITE_REWARD"
  },
  {
    "date": "2025-04-03",
    "amount": -500,
    "type": "ITEM_PURCHASE"
  }
]
```

---

## 📘 학습한 점 요약

- Enum 타입은 문자열이 아닌 Enum 상수로 지정해야 한다 (예: `PointsType.INVITE_REWARD`)
- 포인트 지급 시 이력은 필수 → `Points` 테이블에서 항상 적립/사용 내역을 남긴다
- 사용자 포인트 총액(`user.points`) 업데이트도 함께 수행해야 한다
- 피추천인과 추천인 보상은 한 트랜잭션처럼 함께 처리되는 게 안정적이다

---

## ⏭️ 다음 계획

- 향후 보상 정책이 바뀔 경우 (포인트 양, 지급 조건 등) → 상수/설정파일 분리 고려
- 관리자 페이지에서 추천인/보상 이력 확인 기능 구현 예정