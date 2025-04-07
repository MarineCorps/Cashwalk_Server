# ✅ 캐시워크 클론 캡스톤 프로젝트 기능 구현 현황 (2025-04-07 기준)

> 프론트엔드: Flutter  
> 백엔드: Java Spring Boot (Spring Security, JWT, JPA, Redis, MySQL)  
> 아키텍처: JWT 인증 기반, RESTful API 설계, 클린 아키텍처 및 계층 분리 적용  

---

## 📦 전체 기능 구현 상태 요약

| 대분류 | 세부 기능 | 구현 상태 | 정리 문서 | 개발 환경 |
|--------|-----------|------------|------------|------------|
| 인증 | 카카오 로그인 | ✅ 완료 | `Google_Kakao_Login_Flutter_SpringBoot.md` | Flutter + Spring Boot |
| 인증 | 구글 로그인 | ✅ 완료 | `Google_Kakao_Login_Flutter_SpringBoot.md` | Flutter + Spring Boot |
| 인증 | JWT 인증 + Security 필터 | ✅ 완료 | `social_login_guide.md` | Spring Boot |
| 사용자 | 회원가입 / 내 정보 조회 | ✅ 완료 | 내부 구현 완료 (정리 생략됨) | Spring Boot |
| 걸음 수 | 걸음 수 저장 | ✅ 완료 | `2025-03-26_걸음수_API_구현_기록.md` | Spring Boot |
| 걸음 수 | 오늘 걸음 수 조회 | ✅ 완료 | `2025-03-27_걸음수조회_API_기록.md` | Spring Boot |
| 걸음 수 | 걸음 수 통계 (일/주/월) | ✅ 완료 | `2025-04-04_걸음수통계_API.md` | Spring Boot |
| 포인트 | 잔액 조회 | ✅ 완료 | `2025-03-27_포인트잔액_API_기록.md` | Spring Boot |
| 포인트 | 이력 조회 | ✅ 완료 | `2025-03-29_points_history_API.md` | Spring Boot |
| 포인트 | 걸음 → 포인트 자동 전환 | ✅ 완료 | `2025-03-26_걸음수_API_구현_기록.md` | Spring Boot |
| 초대 | 친구 초대 코드 발급/적용 | ✅ 완료 | `2025-04-04_invite_api_log.md` | Spring Boot |
| 초대 | 추천 보상 지급 | ✅ 완료 | `2025-04-04_invite_reward_api_log.md` | Spring Boot |
| 관리자 | 포인트 지급/차감 | ✅ 완료 | `2025-04-04_admin_points_api_log.md` | Spring Boot |
| 관리자 | 포인트 초기화 | ✅ 완료 | `2025-04-04_admin_reset_points_api_log.md` | Spring Boot |
| 관리자 | 계정 생성 및 ROLE 적용 | ✅ 완료 | `2025-04-05_admin_auth_role_setting.md` | Spring Boot |
| 관리자 | 유저 삭제, 연관데이터 cascade 삭제 | ✅ 완료 | `2025-04-04_admin_api_log.md` | Spring Boot |
| 스토어 | 아이템 목록 조회 | ✅ 완료 | `store_items_api_log.md` | Spring Boot |
| 스토어 | 아이템 교환 | ✅ 완료 | `store_exchange_api_log (1).md` | Spring Boot |
| 광고 | 광고 시청 보상 지급 | ✅ 완료 | `2025-03-30_ads_reward_API.md` | Spring Boot |
| 광고 | 광고 시청 내역 조회 | ✅ 완료 | `2025-03-30_ads_history_API.md` | Spring Boot |
| 커뮤니티 | 게시글 작성/수정/삭제 | ✅ 완료 | `community_feature_summary.md` | Spring Boot |
| 커뮤니티 | 게시글 정렬 (최신/좋아요/댓글) | ✅ 완료 | `community_post_sorting.md` | Spring Boot |
| 커뮤니티 | 게시글에 닉네임 포함 응답 | ✅ 완료 | `post_with_nickname_summary.md` | Spring Boot |
| 커뮤니티 | 게시글 좋아요/비추천 토글 | ✅ 완료 | 정리 생략 | Spring Boot |
| 커뮤니티 | 좋아요/비추천 개수 조회 | ✅ 완료 | `post_reaction_count_api.md` | Spring Boot |
| 커뮤니티 | 게시글 조회수 증가 (중복 방지) | ✅ 완료 | `viewcount_redis_summary.md` | Spring Boot + Redis |
| 커뮤니티 | 댓글 작성/수정/삭제 | ✅ 완료 | `community_comment_api.md` | Spring Boot |
| 커뮤니티 | 게시글 + 댓글 상세조회 | ✅ 완료 | `community_feature_summary.md` | Spring Boot |
| 커뮤니티 | 명예의 전당 보드타입 (LEGEND) | ✅ 완료 | `community_post_sorting.md` | Spring Boot |
| 푸시 | 디바이스 토큰 등록/알림 발송 | ❌ 미구현 | 예정 | FCM + Spring Boot |
| 출석 | 출석 체크 보상 | ✅ 완료 | `2025-04-05_출석이벤트_API_기록.md` | Spring Boot |

---

## 📌 비고

- ✅ 완료 항목: 기능 구현 + 문서 정리까지 마무리됨
- ❌ 미구현 항목: 추후 Firebase 연동 또는 일정에 따라 진행 예정

---

