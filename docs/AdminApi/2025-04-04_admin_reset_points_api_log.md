# ✅ 관리자 포인트 초기화 기능 구현 기록

- 📅 날짜: 2025-04-04
- 🔧 구현 목표: 관리자가 특정 사용자의 포인트를 0으로 초기화할 수 있는 기능 구현

---

## ✅ 기능 요약

| 기능 | 설명 |
|------|------|
| 포인트 초기화 | 특정 사용자의 포인트를 0으로 설정하고 이력 저장 |
| 이력 타입 구분 | `RESET` 타입으로 `points` 테이블에 저장됨 |
| REST API 엔드포인트 | `POST /api/admin/reset-points?userId=1` |

---

## 📁 수정/생성된 파일 목록

| 파일명 | 패키지 | 설명 |
|--------|--------|------|
| `PointsType.java` | `entity` | `RESET` enum 상수 추가 |
| `PointsService.java` | `service` | `resetPoints(User user)` 메서드 추가 |
| `AdminService.java` | `service` | `resetUserPoints(Long userId)` 메서드 추가 |
| `AdminController.java` | `controller` | `@PostMapping("/reset-points")` API 추가 |

---

## 🔄 전체 실행 흐름

1. 관리자에서 `POST /api/admin/reset-points?userId=1` 요청
2. `AdminController` → `AdminService.resetUserPoints(...)` 호출
3. 사용자의 현재 포인트 조회
4. 기존 포인트만큼 음수로 `Points` 기록 추가 (`RESET`)
5. 사용자의 포인트를 0으로 업데이트
6. 응답: `"포인트가 초기화되었습니다."`

---

## 🧪 테스트 결과

- `points` 테이블에 다음과 같은 이력 생성됨:

```
{
  "date": "2025-04-04",
  "amount": -3500,
  "type": "RESET"
}
```

- `/api/points/history`에서도 정상 출력
- 응답 코드: `200 OK`

---

## 🔐 보안 메모

- 현재는 `/api/admin/**` 경로 `permitAll()` 상태
- 추후 운영 시 `@PreAuthorize("hasRole('ADMIN')")` 또는 `user.isAdmin()` 체크 적용 예정

---

## 💡 학습한 점

- 초기화는 지급/차감 로직과 동일하게 `음수 포인트 + 잔액 수정`으로 구현 가능
- 이력 관리를 통해 포인트 변경 내역 추적 가능
- enum 타입 추가로 이력 구분이 명확해짐
