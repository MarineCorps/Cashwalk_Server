# 📝 Community API 명세서

> 게시판 및 댓글 기능에 대한 백엔드 API 명세입니다.  
> 게시글 등록, 수정, 삭제, 조회, 좋아요/싫어요, 댓글 처리 등을 포함합니다.

---

## 📌 게시글 API

### ✅ [POST] /api/community/post
- 게시글 등록
- JWT 필요

**Request Body**
```json
{
  "title": "제목입니다",
  "content": "본문 내용입니다",
  "boardType": "FREE",
  "postCategory": "NORMAL"
}
```

**Response**
```json
{
  "postId": 123
}
```

---

### 📄 [GET] /api/community/posts
- 게시글 목록 조회
- 기본 정렬: 최신순 (page, size 포함)

**Query Params**
- `boardType=FREE`  
- `page=0&size=10`  

**Response (요약)**
```json
{
  "content": [
    {
      "postId": 1,
      "title": "첫 글입니다",
      "nickname": "홍길동",
      "likes": 12
    }
  ]
}
```

---

### 🔍 [GET] /api/community/post/{postId}
- 특정 게시글 상세 조회

---

### 📝 [PATCH] /api/community/post/{postId}
- 게시글 수정

---

### ❌ [DELETE] /api/community/post/{postId}
- 게시글 삭제
- 작성자만 가능

---

### ❤️ [POST] /api/community/post/{postId}/like
- 게시글 좋아요 or 싫어요 처리

**Body**
```json
{
  "status": "LIKE"  // or "DISLIKE"
}
```

---

### 📌 [POST] /api/community/post/{postId}/report
- 게시글 신고

---

## 💬 댓글 API

### ✅ [POST] /api/community/comment
- 댓글 등록

```json
{
  "postId": 1,
  "content": "댓글 내용입니다"
}
```

---

### 📝 [PATCH] /api/community/comment/{commentId}
- 댓글 수정

---

### ❌ [DELETE] /api/community/comment/{commentId}
- 댓글 삭제

---

## ✅ 공통 사항

- 모든 요청은 JWT 토큰 필요: `Authorization: Bearer <token>`
- 응답은 JSON 형식이며, 실패 시 에러 코드와 메시지를 포함합니다.