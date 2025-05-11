
# ✅ 커뮤니티 기능 - 게시글 추천/비추천 개수 조회 기능 구현 정리

- 📅 날짜: 2025-04-06
- 🔧 구현 목표: 게시글에 대한 추천(좋아요) 및 비추천(싫어요) 개수를 조회할 수 있는 API 구현
- 🧩 연관 기능: 좋아요/비추천 토글 API(`/like`, `/dislike`)가 선행되어야 함

---

## 📁 디렉토리/파일 구조 및 역할 요약

| 디렉토리 | 파일명 | 설명 |
|----------|--------|------|
| `entity` | `PostLike.java` | 사용자의 게시글 좋아요/비추천 기록 저장 |
| `repository` | `PostLikeRepository.java` | 게시글별 추천/비추천 개수 집계용 쿼리 수행 |
| `service` | `CommunityService.java` | 비즈니스 로직 처리 및 개수 조회 기능 구현 |
| `dto` | `PostReactionCountResponse.java` | 프론트에 반환할 응답 DTO |
| `controller` | `CommunityController.java` | API 엔드포인트 제공 (`/posts/{id}/reactions`) |

---

## 📌 전체 실행 흐름

1. 프론트에서 게시글 상세 화면에 진입
2. `GET /api/community/posts/{postId}/reactions` 호출
3. `CommunityController`가 `CommunityService`의 조회 메서드 호출
4. `PostLikeRepository`에서 `countByPostIdAndReactionType(...)` 메서드로 각각 count 조회
5. 결과를 `PostReactionCountResponse` DTO로 감싸서 응답

---

## 🔐 보안 (SecurityConfig)

- ✅ 로그인 여부와 관계없이 누구나 개수는 조회할 수 있도록 `permitAll()` 설정되어 있음
```java
.requestMatchers(HttpMethod.GET, "/api/community/posts/**/reactions").permitAll()
```

---

## 🧠 핵심 코드 설명

### 📌 1. PostLike.java (Entity)
```java
public enum ReactionType {
    LIKE, DISLIKE
}

@Entity
public class PostLike {
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private ReactionType reactionType;
}
```

### 📌 2. PostLikeRepository.java
```java
int countByPostIdAndReactionType(Long postId, ReactionType reactionType);
```
> `ReactionType.LIKE`와 `DISLIKE`로 각각 count

---

### 📌 3. CommunityService.java
```java
public PostReactionCountResponse getReactionCounts(Long postId) {
    int likeCount = postLikeRepository.countByPostIdAndReactionType(postId, ReactionType.LIKE);
    int dislikeCount = postLikeRepository.countByPostIdAndReactionType(postId, ReactionType.DISLIKE);
    return new PostReactionCountResponse(likeCount, dislikeCount);
}
```

### 📌 4. PostReactionCountResponse.java
```java
public class PostReactionCountResponse {
    private int likeCount;
    private int dislikeCount;
}
```

---

### 📌 5. CommunityController.java
```java
@GetMapping("/posts/{postId}/reactions")
public PostReactionCountResponse getReactions(@PathVariable Long postId) {
    return communityService.getReactionCounts(postId);
}
```

---

## ✅ 테스트 결과

| 요청 | 결과 |
|------|------|
| `GET /api/community/posts/1/reactions` | `{ "likeCount": 1, "dislikeCount": 1 }` 응답 확인 |
| Postman에서 Header에 Authorization이 없는 경우도 OK |

---

## 📚 배운 점 요약

- 추천/비추천은 별도 테이블로 관리해야 중복 방지, 토글 처리, 히스토리 기록 가능
- `countBy...` 메서드를 통해 쉽게 통계적 데이터를 제공할 수 있음
- 컨트롤러 → 서비스 → 리포지토리 → 응답 DTO 흐름은 REST API의 기본 구조다
