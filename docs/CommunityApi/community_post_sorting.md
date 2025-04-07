# ✅ 커뮤니티 게시글 정렬 기능 구현 (좋아요순 / 댓글순)

- **날짜**: 2025-04-07
- **기능 요약**: 커뮤니티 게시글 목록을 좋아요 수 또는 댓글 수 기준으로 정렬하여 조회하는 기능 구현
- **구현 목표**: 
  - 사용자가 `/api/community/posts?sort=likes` 또는 `?sort=comments`로 요청 시
  - 해당 정렬 기준에 따라 게시글을 정렬해서 페이징 형태로 응답
---

## 📁 디렉토리별 역할 요약

| 디렉토리 | 설명 |
|----------|------|
| `repository` | Native Query로 좋아요 수 / 댓글 수 정렬된 게시글 목록 조회 |
| `service` | 정렬 기준 파라미터로 분기하여 Repository 메서드 호출 |
| `controller` | 정렬 기준 sort 파라미터를 받아 목록 응답 |

---

## 🛠 수정/생성한 파일 목록

- `PostRepository.java`
- `CommunityService.java`
- `CommunityController.java`

---

## 🔄 전체 실행 흐름

1. **클라이언트 요청**: `/api/community/posts?sort=likes` 또는 `?sort=comments`
2. **Controller**: `sort` 파라미터를 받고 `CommunityService`에 전달
3. **Service**:
   - `sort = likes`인 경우 → `PostRepository.findAllOrderByLikes(...)` 호출
   - `sort = comments`인 경우 → `PostRepository.findAllOrderByCommentCount(...)` 호출
4. **Repository**:
   - Native SQL 쿼리를 사용해 `post_likes` 또는 `comments` 테이블에서 개수 조회
   - `Page<Object[]>` 결과를 DTO로 변환하여 반환
5. **응답**: 프론트엔드에 정렬된 게시글 목록 + 페이징 정보 포함 응답

---

## 🔍 주요 코드 설명

### 1. `PostRepository.java`

```java
@Query(value = """
    SELECT p.id, p.title, p.content, p.image_url, p.user_id, p.created_at,
           u.nickname,
           (SELECT COUNT(*) FROM post_likes pl WHERE pl.post_id = p.id AND pl.status = 'LIKE') AS like_count,
           (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) AS comment_count
    FROM post p
    JOIN user u ON p.user_id = u.id
    WHERE (:boardType IS NULL OR p.board_type = :boardType)
    ORDER BY like_count DESC, p.created_at DESC
    """,
    countQuery = """
    SELECT COUNT(*)
    FROM post p
    WHERE (:boardType IS NULL OR p.board_type = :boardType)
    """,
    nativeQuery = true)
Page<Object[]> findAllOrderByLikes(@Param("boardType") String boardType, Pageable pageable);
```

→ 좋아요 수 기준 정렬

---

### 2. `CommunityService.java`

```java
public Page<PostResponseDto> getPostList(String sort, BoardType boardType, Pageable pageable) {
    if ("likes".equals(sort)) {
        return mapToDto(postRepository.findAllOrderByLikes(boardType != null ? boardType.name() : null, pageable));
    } else if ("comments".equals(sort)) {
        return mapToDto(postRepository.findAllOrderByCommentCount(boardType != null ? boardType.name() : null, pageable));
    } else {
        return postRepository.findAllByBoardTypeOrderByCreatedAtDesc(boardType, pageable)
                .map(PostResponseDto::fromEntity);
    }
}
```

→ 정렬 기준별 분기 처리

---

### 3. `CommunityController.java`

```java
@GetMapping("/posts")
public ResponseEntity<Page<PostResponseDto>> getPostList(
    @RequestParam(defaultValue = "recent") String sort,
    @RequestParam(required = false) BoardType boardType,
    Pageable pageable
) {
    return ResponseEntity.ok(communityService.getPostList(sort, boardType, pageable));
}
```

---

## ✅ 테스트 결과

- `GET /api/community/posts?sort=likes` → 좋아요 순으로 게시글 정렬 성공 ✅
- `GET /api/community/posts?sort=comments` → 댓글 순으로 게시글 정렬 성공 ✅
- 페이지네이션 정보도 정상적으로 포함됨 (Postman 확인 완료)

---

## 📚 학습한 점 정리

- Native SQL로 COUNT 연산 결과를 정렬 기준으로 사용할 수 있음
- `Page<Object[]>` 결과는 DTO로 매핑 필요
- `@Query` 사용 시 countQuery 꼭 명시
- `@RequestParam` 기본값 설정을 통해 정렬 기준의 유연한 처리 가능