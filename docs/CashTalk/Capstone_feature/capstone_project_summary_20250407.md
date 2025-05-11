# 📦 캡스톤 프로젝트 기능 구현 현황 및 디렉토리 정리 (2025-04-07 기준)

---

## ✅ 구현 완료 기능 목록

| 기능 분류 | 상세 내용 |
|----------|-----------|
| 🔐 인증/회원가입 | - 구글 로그인 (idToken → Spring 검증, JWT 발급)<br>- 카카오 로그인 연동<br>- JWT 인증 필터 구현 |
| 👤 사용자 | - 내 정보 조회 API<br>- 사용자 정보 DTO 구성 완료 |
| 🦶 걸음 수 | - 걸음 수 저장 API<br>- 걸음 수 통계 API (일별 합계, 주간 평균 등) |
| 💰 포인트 | - 포인트 적립/사용/조회 기능<br>- 포인트 내역 DTO 분리 |
| 📺 광고 | - 광고 시청 후 보상 지급 |
| 🛍️ 스토어 | - 아이템 조회 API<br>- 포인트로 아이템 교환 API |
| 👫 친구 초대 | - 추천 코드 생성 및 적용<br>- 추천 보상 포인트 지급 로직 |
| 🛠️ 관리자 기능 | - 포인트 수동 지급/차감/초기화<br>- 관리자 계정 ROLE_ADMIN 적용 |
| 📝 커뮤니티 | - 게시글 작성/조회/수정/삭제<br>- 댓글 작성/조회/삭제<br>- 게시글 좋아요/비추천 토글<br>- 댓글 좋아요/비추천 토글<br>- 게시글 정렬 (좋아요순/댓글순)<br>- 게시글 작성자 닉네임 표시 |

---

## 🔜 앞으로 구현할 기능 목록

| 기능 분류 | 상세 내용 |
|----------|-----------|
| 🎯 이벤트 | - 출석 체크 기능<br>- 챌린지/룰렛 등 |
| 📢 푸시 알림 | - FCM 연동<br>- 사용자별 푸시 발송 기능 |
| 📊 통계 시각화 | - 운동 리포트 UI<br>- 포인트/걸음 수 트렌드 |
| 🧠 OpenAI 연동 | - 건강 팁 추천 (운동, 수면 등) |
| 💬 커뮤니티 추가 기능 | - 조회수 증가 로직<br>- 글/댓글 신고 기능<br>- 글 검색 기능<br>- 명예의 전당(LEGEND 게시판) 분리 |

---

## 📁 현재 IntelliJ 디렉토리 구조 요약

```
com.example.cashwalk
├── controller
│   ├── AuthController.java
│   ├── UserController.java
│   ├── StepsController.java
│   ├── PointsController.java
│   ├── AdsController.java
│   ├── EventsController.java
│   ├── StoreController.java
│   ├── InviteController.java
│   ├── PushController.java
│   ├── AdminController.java
│   └── CommunityController.java
├── service
│   ├── AuthService.java
│   ├── UserService.java
│   ├── StepsService.java
│   ├── PointsService.java
│   ├── AdsService.java
│   ├── EventsService.java
│   ├── StoreService.java
│   ├── InviteService.java
│   ├── PushService.java
│   ├── AdminService.java
│   └── CommunityService.java
├── repository
│   ├── UserRepository.java
│   ├── StepsRepository.java
│   ├── PointsRepository.java
│   ├── AdsRepository.java
│   ├── EventsRepository.java
│   ├── StoreRepository.java
│   ├── InviteRepository.java
│   ├── PushRepository.java
│   └── PostRepository.java
├── entity
│   ├── User.java
│   ├── Steps.java
│   ├── Points.java
│   ├── Ads.java
│   ├── Event.java
│   ├── StoreItem.java
│   ├── Invite.java
│   ├── PushNotification.java
│   └── Post.java
├── dto
│   ├── AuthRequest.java
│   ├── AuthResponse.java
│   ├── UserDto.java
│   ├── StepsDto.java
│   ├── PointsDto.java
│   ├── AdsDto.java
│   ├── EventDto.java
│   ├── StoreItemDto.java
│   ├── InviteDto.java
│   ├── PushNotificationDto.java
│   └── PostResponseDto.java
├── config
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   └── CorsConfig.java
├── security
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
├── utils
│   ├── PasswordUtil.java
│   ├── JwtUtil.java
│   └── DateUtil.java
```

---

📌 **비고**
- 게시글 닉네임 표시는 Native Query로 `JOIN user u ON p.user_id = u.id` 처리
- PostResponseDto에 `nickname` 필드 추가하여 프론트에서 사용자 닉네임 노출 가능
- Post 엔티티에는 nickname을 저장하지 않음 (정규화 유지 목적)