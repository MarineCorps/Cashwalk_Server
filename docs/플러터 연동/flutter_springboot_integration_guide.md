
# 📱 Flutter ↔ Spring Boot 연동 가이드 (Capstone)

날짜: 2025-04-13  
작성자: ChatGPT

---

## ✅ 프로젝트 개요

Flutter 앱과 Spring Boot 백엔드를 연동하여 다음 기능을 완성합니다:

- 친구 검색 / 추가 / 목록 / 삭제
- 채팅방 생성 및 메시지 주고받기 (WebSocket)
- 푸시 토큰 등록 및 수신 (Firebase 예정)

---

## 🧱 백엔드 구조 요약

- Base Package: `com.example.cashwalk`
- 주요 디렉토리:

| 디렉토리 | 설명 |
|----------|------|
| `controller` | REST API 및 WebSocket 핸들러 |
| `service` | 핵심 비즈니스 로직 처리 |
| `dto` | Flutter ↔ Server 데이터 구조 정의 |
| `entity` | JPA 엔티티 (DB 매핑) |
| `repository` | 데이터 접근 계층 |
| `config`, `security` | 보안, JWT 인증, WebSocket 설정 |

---

## 🔐 인증 방식 (JWT)

- Flutter 로그인 후 JWT 토큰을 저장 (`Authorization: Bearer <토큰>`)
- 모든 API 호출 시 이 토큰을 **헤더에 포함**
- WebSocket도 토큰을 함께 보냄 (STOMP 헤더)

---

## 🌐 API 연동 요약

### 1. 친구 추천 코드 검색
```
GET /api/friends/search?code=XXXX
```

### 2. 친구 추가
```
POST /api/friends/add
Body: { "inviteCode": "XXXX" }
```

### 3. 친구 목록
```
GET /api/friends/list
```

### 4. 친구 삭제
```
DELETE /api/friends/delete/{friendId}
```

### 5. 친구 상태 확인
```
GET /api/friends/status/{friendId}
Response: true / false
```

---

## 💬 WebSocket 연결 방식

- 연결 URL:
```
ws://<서버주소>/ws/chat
```

- 구독 경로:
```
/topic/room.{상대ID}
```

- 메시지 발신 경로:
```
/app/chat.send
```

### 메시지 DTO (ChatMessageDto)
```json
{
  "senderId": 1,
  "roomId": "2",
  "content": "안녕!",
  "fileUrl": null,
  "type": "TEXT"
}
```

---

## 🧠 주의 사항

- 모든 API는 JWT 필요 (`@AuthenticationPrincipal` 사용)
- WebSocket도 토큰 인증 필수 (`JwtHandshakeInterceptor` 구현됨)
- `profileImage`, `inviteCode` 등은 User 엔티티에서 관리됨
- Flutter에서 오류 발생 시 백엔드 변수명, 엔드포인트 정확히 확인

---

## 📌 Flutter 연동 시 필요한 패키지

- `http`
- `stomp_dart_client`
- `shared_preferences` 또는 `flutter_secure_storage`
- (선택) `image_picker`, `dio`

---

## 🔚 요약

Spring Boot 백엔드는 완전히 준비되어 있으며,
Flutter에서 위 경로 및 데이터 형식에 맞게 요청하면 연동이 성공합니다.
이 문서의 내용은 캡스톤 프로젝트의 표준으로 유지됩니다.
