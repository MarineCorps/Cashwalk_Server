# ✅ 관리자 포인트 지급/차감 기능 구현 기록

- 📅 날짜: 2025-04-04
- 🔧 구현 목표: 관리자가 사용자에게 포인트를 수동으로 지급하거나 차감하는 기능을 구현한다.

---

## ✅ 기능 요약

| 기능 | 설명 |
|------|------|
| 사용자 목록 조회 | 관리자 페이지에서 전체 사용자 확인 |
| 포인트 수동 지급/차감 | 특정 사용자에게 + 또는 - 포인트 적용 |
| 포인트 이력 반영 | 지급/차감 이력이 `points` 테이블에 `MANUAL` 타입으로 기록됨 |
| 보안 정책 | 현재는 permitAll() 상태, 운영 시 `ROLE_ADMIN` 적용 예정 |

---

## 📁 수정/생성된 파일 목록

| 파일명 | 패키지 | 설명 |
|--------|--------|------|
| `AdminController.java` | `controller` | 관리자용 API 정의 |
| `AdminService.java` | `service` | 사용자 목록, 포인트 지급/차감 로직 처리 |
| `ModifyPointsRequest.java` | `dto` | 포인트 조정 요청 DTO |
| `UserDto.java` | `dto` | 사용자 정보 응답 DTO, from(User) 메서드 포함 |
| `PointsService.java` | `service` | `addReward(...)` 메서드 추가 |
| `SecurityConfig.java` | `config` | `/api/admin/**` 경로 permitAll() 임시 허용 |

---

## 🔄 전체 흐름 요약

### ▶ 사용자 목록 조회

1. GET `/api/admin/users`
2. `AdminController` → `AdminService.getAllUsers()`
3. 사용자 리스트 조회 후 DTO 변환 → 응답

### ▶ 포인트 지급/차감

1. POST `/api/admin/points`
2. Body: `userId`, `amount`, `description`
3. `AdminService.modifyUserPoints()` → `PointsService.addReward()`
4. 사용자 포인트 업데이트 + 포인트 이력 저장

---

## 🧪 테스트 결과

- DB `points` 테이블에 `MANUAL` 타입 이력 정상 저장됨
- `/api/points/history` 에 `MANUAL`, `INVITE_REWARD`, `AD_REWARD` 모두 정상 출력됨
- Postman 테스트 성공

---

## 🔐 보안 메모

- 현재 상태: `/api/admin/**` → `permitAll()`로 테스트 중
- 운영 시 보완 예정:
  - `@PreAuthorize("hasRole('ADMIN')")` 방식 적용
  - 또는 사용자 엔티티에 `isAdmin` 필드 활용하여 `user.isAdmin()` 체크

---

## 💡 학습한 점

- Spring Security 경로 설정 (`permitAll`, `authenticated`)
- DTO 변환 로직이 누락된 데이터를 필터링할 수 있다는 점
- 수동 포인트 지급/차감도 `Points` 테이블 이력화 패턴에 포함되어야 함
