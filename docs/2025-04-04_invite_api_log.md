# ✅ 친구 초대 기능 API 개발 기록

---

## 📆 날짜

2025년 4월 4일 (금)

---

## 🎯 구현 목표

- 사용자가 자신의 추천 코드를 조회하거나,  
  다른 사용자의 추천 코드를 입력하여 추천 관계를 맺을 수 있도록 한다.
- 추천 적용 시 중복 사용 방지 및 본인 코드 사용 금지 로직 포함
- 추후 보상 포인트 지급 기능과 연동 예정

---

## 📁 디렉토리별 역할 요약

| 디렉토리 | 역할 |
|----------|------|
| `entity` | Invite 테이블 정의 (referrer, invitee, invite_code, applied_at) |
| `dto` | 추천 코드 조회/적용 응답 구조 정의 (`InviteDto`) |
| `repository` | 추천 코드 및 추천 관계 DB 조회 (`InviteRepository`) |
| `service` | 추천 코드 생성, 유효성 검증, 추천 코드 적용 로직 (`InviteService`) |
| `controller` | 클라이언트 요청 처리 (추천 코드 조회 및 적용 API) |
| `config` | 인증된 사용자만 초대 기능을 사용할 수 있도록 보안 설정 |

---

## 🧾 수정/생성한 파일 목록

| 파일명 | 위치 | 설명 |
|--------|------|------|
| `Invite.java` | `entity` | 추천 관계 엔티티. 추천인(referrer), 피추천인(invitee), 코드 포함 |
| `InviteDto.java` | `dto` | 코드 및 보상 여부를 포함한 응답 DTO |
| `InviteRepository.java` | `repository` | 코드/추천인/피추천인 기반 조회 쿼리 정의 |
| `InviteService.java` | `service` | 추천 코드 조회/적용 비즈니스 로직 구현 |
| `InviteController.java` | `controller` | API: `/api/invite/code`, `/api/invite/apply` |
| `SecurityConfig.java` | `config` | `/api/invite/**` 경로에 대해 인증 처리 설정 추가 |

---

## 🔁 전체 실행 흐름

### 1. 추천 코드 조회 (`GET /api/invite/code`)

1. JWT 인증된 사용자로 요청
2. 기존 추천 코드가 있으면 반환, 없으면 UUID 기반 코드 생성
3. invite 테이블에 저장 (`invitee = null`)
4. 추천 코드 응답

### 2. 추천 코드 입력 (`POST /api/invite/apply?code=XXXXXXX`)

1. JWT 인증된 사용자가 타인의 추천 코드를 입력
2. 자기 코드 입력 시 → 예외
3. 이미 추천 받은 경우 → 예외
4. 코드 유효성 검증
5. 추천인-피추천인 관계 저장 (invite 테이블에 invitee, appliedAt 업데이트)
6. 보상 예정 → 추후 `PointsService` 연동

---

## 🧩 주요 코드 예시

### 📌 InviteService.java - 추천 코드 생성

```java
public InviteDto getOrCreateInviteCode(Long userId) {
    User user = findUserById(userId);
    Optional<Invite> existing = inviteRepository.findByReferrer(user);
    if (existing.isPresent()) {
        return new InviteDto(existing.get().getInviteCode(), false);
    }

    String newCode = generateInviteCode();
    Invite invite = new Invite();
    invite.setReferrer(user);
    invite.setInvitee(null); // 추천 받은 사람은 아직 없음
    invite.setInviteCode(newCode);
    inviteRepository.save(invite);

    return new InviteDto(newCode, false);
}
```

---

### 📌 InviteService.java - 추천 코드 적용

```java
public InviteDto applyInviteCode(Long userId, String code) {
    User invitee = findUserById(userId);

    // 자기 코드 입력 방지
    Optional<Invite> myInvite = inviteRepository.findByReferrer(invitee);
    if (myInvite.isPresent() && myInvite.get().getInviteCode().equals(code)) {
        throw new IllegalArgumentException("자기 자신의 추천 코드는 사용할 수 없습니다.");
    }

    // 이미 추천 받은 경우 방지
    if (inviteRepository.findByInvitee(invitee).isPresent()) {
        throw new IllegalStateException("이미 추천을 받은 사용자입니다.");
    }

    // 추천 코드 유효성 검증
    Invite invite = inviteRepository.findByInviteCode(code)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 추천 코드입니다."));

    // 피추천인 및 추천 시간 설정
    invite.setInvitee(invitee);
    invite.setAppliedAt(LocalDateTime.now());
    inviteRepository.save(invite);

    return new InviteDto(code, true);
}
```

---

## 📡 Postman 테스트 결과

| 요청 | 결과 |
|------|------|
| `/api/invite/code` (추천 코드 조회) | ✅ 200 OK, 추천 코드 생성 또는 조회됨 |
| `/api/invite/apply?code=XXXXXX` | ✅ 200 OK, 추천 코드 적용됨 |
| 자기 코드 입력 | ❌ 400, "자기 자신의 추천 코드는 사용할 수 없습니다" |
| 중복 적용 | ❌ 400, "이미 추천을 받은 사용자입니다" |
| 잘못된 코드 | ❌ 400, "유효하지 않은 추천 코드입니다" |

---

## 📘 학습한 점 요약

- JPA에서 `nullable = false` 제약은 insert 시 반드시 값이 필요함 → 설계에 맞게 `nullable = true` 설정 필요
- 추천 코드 적용 시 `insert`가 아닌 기존 객체에 `update` 방식으로 처리해야 unique 제약을 위반하지 않음
- JWT 미포함 요청은 `anonymous` 사용자로 인식됨 → Security 설정 및 헤더 확인 중요
- `UUID.randomUUID()` 로 추천 코드 생성 시 길이를 잘라서 사용하는 것이 안전함

---

## ⏭️ 다음 계획

- 추천 코드 적용 시 추천인(referrer)과 피추천인(invitee) **모두에게 포인트 보상 지급 기능 추가 예정**
- `PointsService` 연동하여 포인트 적립 로직 구현