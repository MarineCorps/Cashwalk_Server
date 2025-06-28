# 🔐 Auth & User API

> 로그인, 회원가입, 유저 프로필, 친구 초대 등 사용자 인증/관리 관련 API입니다.

---

## ✅ Auth

### [POST] /api/auth/login
- 소셜 로그인 → JWT 발급

### [POST] /api/auth/reissue
- access token 만료 시 refresh token으로 재발급

### [POST] /api/auth/register
- 최초 로그인 시 사용자 정보 등록

---

## 👤 User

### [GET] /api/user/me
- 내 프로필 조회

### [PATCH] /api/user/profile
- 닉네임, 키, 몸무게 등 프로필 수정

---

## ✉️ Invite

### [GET] /api/invite/code
- 나의 초대 코드 조회

### [POST] /api/invite/use
- 친구의 초대 코드 입력 → 보상 지급