## 💬 캐시톡 API

> 실시간 채팅, 친구 추가, 랭킹, 행운캐시 전송 등 캐시워크의 소셜 기능 API입니다.

### 💬 채팅

#### \[GET] `/api/chat/rooms`

```json
[
  {
    "roomId": 1,
    "opponentId": 12,
    "opponentNickname": "철수",
    "lastMessage": "잘 지내?",
    "lastTime": "2025-06-01T14:22:00"
  }
]
```

#### \[POST] `/api/chat/room`

```json
{
  "receiverId": 12,
  "content": "안녕!"
}
```

#### \[GET] `/api/chat/room/{id}`

```json
[
  {
    "messageId": "abc123",
    "senderId": 1,
    "content": "안녕하세요!",
    "fileUrl": null,
    "createdAt": "2025-06-01T13:45:00"
  }
]
```

---

### 👥 친구 기능

#### \[GET] `/api/friends`

```json
[
  {
    "id": 2,
    "nickname": "영희",
    "profileImage": "https://cdn.com/image.jpg",
    "inviteCode": "XZ29YW"
  }
]
```

#### \[POST] `/api/friends/request`

```json
{
  "userId": 3
}
```

#### \[POST] `/api/friends/accept`

```json
{
  "userId": 3
}
```

---

### 🏆 랭킹

#### \[GET] `/api/ranking/steps`

#### \[GET] `/api/ranking/points`

```json
[
  {
    "userId": 1,
    "nickname": "철수",
    "profileImage": "https://cdn.com/pic.png",
    "points": 4300,
    "rank": 2
  }
]
```

---

### 🍀 행운 캐시

#### \[POST] `/api/chat/lucky-cash`

```json
{
  "receiverId": 5,
  "content": "행운이 가득하길!"
}
```

#### \[GET] `/api/chat/lucky-friends`

```json
[
  {
    "messageId": "luck123",
    "senderId": 3,
    "content": "오늘도 행운가득!",
    "fileUrl": null,
    "createdAt": "2025-06-01T08:30:00",
    "opened": false,
    "expired": false
  }
]
```

---

## 📱 화면 구성

|                                                            홈/걸음수/보상                                                            |                                                            채팅/러닝 기록                                                            |
| :----------------------------------------------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------------------: |
| <img src="https://user-images.githubusercontent.com/80824750/208456048-acbf44a8-cd71-4132-b35a-500047adbe1c.gif" width="400"/> | <img src="https://user-images.githubusercontent.com/80824750/208456234-fb5fe434-aa65-4d7a-b955-89098d5bbe0b.gif" width="400"/> |

---
