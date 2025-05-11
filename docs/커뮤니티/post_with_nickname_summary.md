# ✅ 게시글 응답에 작성자 닉네임 포함 로직 정리 (2025-04-07)

## 🔸 구현 목표
- 게시글 리스트를 조회할 때 `userId`와 함께 작성자의 `nickname`도 포함하여 프론트로 전달한다.
- 프론트는 userId로 권한 체크(수정/삭제), nickname은 UI 출력용으로 사용한다.

---

## 🗂 수정/생성된 파일 목록

| 파일명 | 설명 |
|--------|------|
| `PostRepository.java` | 게시글 + 사용자 닉네임을 함께 조회하는 Native SQL 쿼리 작성 |
| `PostResponseDto.java` | Object[]로 받은 결과에서 nickname까지 포함해 DTO로 변환 |
| `CommunityService.java` | repository에서 받은 Object[]을 PostResponseDto로 변환 |
| `CommunityController.java` | 게시글 리스트 조회 시 PostResponseDto 리스트 반환 |

---

## 🧠 전체 실행 흐름

### 1. PostRepository
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
    """, nativeQuery = true)
Page<Object[]> findAllOrderByLikes(@Param("boardType") String boardType, Pageable pageable);
```

### 2. CommunityService
```java
List<PostResponseDto> dtos = posts.map(PostResponseDto::fromObjectArray).getContent();
```

### 3. PostResponseDto
```java
public static PostResponseDto fromObjectArray(Object[] row) {
    return PostResponseDto.builder()
        .id(((BigInteger) row[0]).longValue())
        .title((String) row[1])
        .content((String) row[2])
        .imageUrl((String) row[3])
        .userId(((BigInteger) row[4]).longValue())
        .createdAt((Timestamp) row[5])
        .nickname((String) row[6])
        .likeCount(((BigInteger) row[7]).intValue())
        .commentCount(((BigInteger) row[8]).intValue())
        .build();
}
```

---

## ✅ 응답 예시 (PostResponseDto)

```json
[
  {
    "id": 1,
    "title": "오늘도 만보 도전",
    "content": "10,000보 달성!",
    "imageUrl": null,
    "userId": 42,
    "nickname": "김산책",
    "createdAt": "2025-04-07T13:00:00",
    "likeCount": 12,
    "commentCount": 5
  }
]
```

---

## 📌 결론

- nickname 필드는 `post 테이블`이 아닌, `JOIN된 user 테이블의 nickname`을 가져오는 구조다.
- 따라서 **Post 엔티티에 nickname 필드는 필요 없다.**
- PostResponseDto에만 포함되면 충분하다.

---

## 📚 배운 점

- Native SQL로 원하는 필드를 자유롭게 조합해서 가져올 수 있음
- DTO 변환 시 Object[] 배열에서 타입을 명확히 캐스팅해야 오류가 안 남