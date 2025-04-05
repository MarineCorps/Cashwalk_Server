# ✅ 관리자 계정 생성 및 ROLE_ADMIN 인증 설정 기록

## 📅 날짜
2025-04-05

---

## 🔧 구현 목표
- 관리자 계정을 직접 DB에 생성
- `ROLE_ADMIN` 권한 기반으로 인증 처리
- 관리자 계정으로 로그인 후 API 접근 가능하게 설정
- 관련 오류 (403, 인증 실패 등) 해결 및 테스트 완료

---

## 📂 디렉토리별 수정 요약

| 디렉토리 | 파일명 | 설명 |
|----------|--------|------|
| `config` | `SecurityConfig.java` | 관리자 접근 허용 설정 추가 |
| `security` | `CustomUserDetails.java` | 권한 목록 반환에 `ROLE_` prefix 포함되도록 수정 |
| `security` | `JwtTokenProvider.java` | 토큰 생성 시 권한 정보를 Claims 에 포함 |
| `controller` | `AdminController.java` | `/api/admin/**` 경로로 관리자 API 제공 |

---

## 🧬 SQL: 관리자 계정 직접 삽입

```sql
INSERT INTO user (
  id, email, password, nickname, role, created_at, points, total_points
) VALUES (
  1,
  'admin@cashwalk.com',
  '$2a$10$DGirxwNSTHvFjxTJ9lpmj.8Qa8pomFNCjKObzP19g4vc2PEHFOSra', -- 123456 암호화된 값
  '관리자',
  'ROLE_ADMIN',
  NOW(),
  0,
  0
);
```

⚠️ 비밀번호는 로그인 기능이 `BCrypt`로 검증되므로 반드시 암호화된 값이어야 함

---

## 🔐 `SecurityConfig.java` 주요 설정

```java
.authorizeHttpRequests()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers(...).permitAll()
    .anyRequest().authenticated()
```

## ✅ `CustomUserDetails.java` 변경

```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
}
```

## ✅ 토큰 안에 role 포함 확인 (`JwtTokenProvider.java`)

```java
claims.put("role", user.getRole());
```

---

## ✅ Postman 테스트 결과

### 🔑 로그인
- ✅ 성공
- `email`: admin@cashwalk.com
- `password`: 123456

### 🔐 관리자 API 접근
- `GET /api/admin/users`
- 헤더: `Authorization: Bearer <jwt>`
- ✅ 200 OK 확인
- 응답: 전체 유저 목록 조회 성공

---

## 📘 학습한 점 요약

- Spring Security 에서 `hasRole("ADMIN")`은 내부적으로 `"ROLE_ADMIN"`과 매칭됨.
- `User.role` 필드에는 반드시 `ROLE_` prefix 포함된 값 저장해야 작동함.
- SecurityConfig 설정만으로는 권한 체크가 되지 않으며, `UserDetails.getAuthorities()`도 수정 필요.
- 직접 암호화한 비밀번호 삽입은 login 실패의 원인이 되므로 Postman 등에서 회원가입 API를 사용하는 것이 안전.

---

✔️ 관리자 기능이 완전히 동작하며, 토큰 기반 인증도 성공적으로 작동함.