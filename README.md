# 🏃‍♂️ CashWalk Server (Spring Boot)

"걸기만 해도 돈을벌고 건강해진다!"
Flutter 기반 CashWalk 클론 앱의 백엔드 서버입니다. 걸음 수적립, 커뮤니티, 실시간 채팅, 산책 리워드, \러닝 기록 기능을 통합한 건강 리워드 플랫폼입니다.

---

## 🚀 주요 기능

| 기능 영역      | 설명                                        |
| ---------- | ----------------------------------------- |
| 👣 걸음 수 적립 | 하루 걸음 수 기반 포인트 적립 (최대 100P), 실시간 누적       |
| 🗌️ 산책 리워드 | 공원 반경 250m 접근 → 10P 적립 (일 1회 제한)          |
| 🏃 러닝 기록  | 거리, 시간, 건데, 카로리, 페이스 기록 및 조회              |
| 🗈️ 캐시톡 채팅 | 친구와 1:1 STOMP 채팅, 행운 캐시 보내기/받기            |
| 📬 친구 기능   | 친구 요청, 수락, 차단, 초대코드 기반 검색                |
| 📝 커뮤니티    | 게시글, 댓글, 좋아요/싫어요, 북마크, 신고 기능              |
| 🔔 푸시 알림   | Firebase 연동 실시간 알림 (Flutter + Android 대응) |

---

## ⚙️ 기술 스택

* **Language**: Java 17
* **Framework**: Spring Boot 3.x, Spring Security, WebSocket (STOMP)
* **Database**: MySQL 8, JPA (QueryDSL 사용)
* **Cache**: Redis
* **Authentication**: JWT (Access + Refresh Token)
* **Push**: Firebase Cloud Messaging (FCM)
* **Infra**: EC2 (Ubuntu)

---

## 🔐 JWT 기반 인증

* `/api/auth/login` → JWT 발급 (access + refresh)
* 모든 API는 `Authorization: Bearer <token>` 헤더 필요
* 토큰 만료 시 `/api/auth/refresh` 호출으로 갱신

---

## 🧱️ 프로젝트 구조

```
com.example.cashwalk
👒 config/           # 보안, WebSocket, Redis 설정
👒 controller/       # API 연딨포인트
👒 dto/              # 요청/응답 DTO
👒 entity/           # JPA 엔티티
👒 repository/       # DB 액세스, QueryDSL 구현체
👒 security/         # JWT 인증 관련 클래스
👒 service/          # 비즈니스 로지크
👒 utils/            # 푸시 알림, 날짜 계산 등 유틸리티
```

---

## 👤 개발자

| 이름  | 역할                                                    |
| --- | ----------------------------------------------------- |
| 김인호 | 전체 기능 기획 및 구현 (SpringBoot(100%) + Flutter(50%)) |
