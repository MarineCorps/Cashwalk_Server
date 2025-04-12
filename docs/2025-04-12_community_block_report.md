
# ✅ 커뮤니티 차단 및 댓글/좋아요 필터링 기능 구현 정리

- 날짜: 2025-04-12
- 구현 범위: 사용자 차단, 댓글/게시글 필터링, 좋아요/댓글 수 동기화
- 대상 디렉토리: controller, service, repository, entity, dto

---

## ✅ 기능 요약

### 📌 1. 사용자 차단 기능
- 로그인한 유저가 다른 유저를 차단/해제할 수 있음
- 차단된 유저의 게시글, 댓글이 모두 숨김 처리됨

### 📌 2. 게시글 필터링
- 게시글 목록 검색 시, 차단한 유저의 글은 조회되지 않음
- QueryDSL에서 `NOT IN blockedUserIds` 조건 적용

### 📌 3. 댓글 필터링
- 댓글 목록 조회 시, 차단 유저가 쓴 댓글/답댓글도 모두 제외
- `comment.getUser().getId()` 및 `comment.getParent().getUser().getId()` 필터 적용

### 📌 4. 좋아요/댓글 수 실시간 반영
- 게시글에 댓글 추가/삭제 시 → `comment_count` 직접 갱신
- 게시글에 좋아요/취소 시 → `like_count` 직접 갱신

---

## ✅ 수정한 주요 메서드

### 📄 CommunityService.java

- `getPostDetail(Long postId, Long currentUserId)`
  - 댓글은 `commentService.getCommentsByPostId(...)` 를 사용하여 필터링 적용
- `likePost`, `dislikePost` 메서드에서 `post.setLikeCount(...)` 수동 증가/감소 처리

### 📄 CommentService.java

- `getCommentsByPostId(Long postId, Long currentUserId)`
  ```java
  .filter(comment -> !blockedUserIds.contains(comment.getUser().getId()))
  .filter(comment -> comment.getParent() == null || !blockedUserIds.contains(comment.getParent().getUser().getId()))
  ```
  - 원댓글, 답댓글 모두 차단 유저이면 제거

- `createComment`, `deleteComment` 에서 `post.commentCount += 1 / -1` 갱신

---

## ✅ 테스트 시나리오

1. A 유저가 B 유저를 차단  
2. 게시글 검색 시 B 유저 글이 목록에 안 나옴  
3. 게시글 상세 조회 시 B 유저 댓글도 안 나옴  
4. 좋아요/댓글 수 반영 확인  
5. 차단 해제 후 다시 글/댓글이 정상적으로 보이는지 확인

---

## ✅ 학습한 점

- 실무에서 대부분 차단 유저의 콘텐츠는 완전히 필터링함 (답댓글 포함)
- 실시간 카운트 처리는 수동으로 갱신하는 구조가 중소 서비스에 적합
- 향후 Redis를 사용해 캐시 기반 카운팅 방식으로 확장 가능

