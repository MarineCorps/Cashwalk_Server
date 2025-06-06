# 💬 캐시톡 API

> 실시간 채팅, 친구 추가, 랭킹, 행운캐시 전송 등 캐시워크의 소셜 기능 API입니다.

---

## 💬 채팅

### [GET] /api/chat/rooms
- 내 채팅방 목록 조회

### [POST] /api/chat/room
- 채팅방 생성

### [GET] /api/chat/room/{id}
- 특정 채팅방 메시지 조회

---

## 👥 친구 기능

### [GET] /api/friends
- 친구 목록 조회

### [POST] /api/friends/request
- 친구 요청

### [POST] /api/friends/accept
- 친구 수락

---

## 🏆 랭킹

### [GET] /api/ranking/steps
- 걸음 수 랭킹 조회

### [GET] /api/ranking/points
- 포인트 랭킹 조회

---

## 🍀 행운 캐시

### [POST] /api/chat/lucky-cash
- 친구에게 행운 캐시 전송

### [GET] /api/chat/lucky-friends
- 오늘의 행운 친구 목록 조회