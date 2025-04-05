# 📘 커뮤니티 게시판 + 댓글 기능 정리 (Spring Boot)

> **작성일:** 2025-04-06<br>
> **목표:** 게시판 + 댓글 기능 전체 흐름, 파일 구조, 코드 원리, DTO/Entity 관계, 테스트 결과 및 배운점까지 전부 상세히 정리한다.

---

## ✅ 구현 기능 요약

| 구분 | 기능명 | 상태 |
|------|--------|------|
| 게시글 | 게시글 작성 (이미지 포함) | ✅ 완료 |
| 게시글 | 게시글 목록 조회 (페이징) | ✅ 완료 |
| 게시글 | 게시글 상세 조회 (댓글 포함) | ✅ 완료 |
| 게시글 | 게시글 수정 / 삭제 | ✅ 완료 |
| 댓글 | 댓글 작성 / 목록 조회 | ✅ 완료 |
| 연관관계 | 게시글-댓글, 사용자-댓글 | ✅ 완료 |
| 예외처리 | 게시글 없음, 사용자 없음 | ✅ 완료 |

---

## 📁 디렉토리 구조

```
com.example.cashwalk
├── controller
│   └── CommunityController.java
│   └── CommentController.java
├── service
│   └── CommunityService.java
│   └── CommentService.java
├── repository
│   └── PostRepository.java
│   └── CommentRepository.java
├── entity
│   └── Post.java
│   └── Comment.java
├── dto
│   └── PostResponseDto.java
│   └── PostDetailResponseDto.java
│   └── CommentRequestDto.java
│   └── CommentResponseDto.java
├── exception
│   └── PostNotFoundException.java
│   └── GlobalExceptionHandler.java
```

---

## 🔁 전체 흐름 요약

### 📌 게시글 상세 + 댓글 포함 응답 흐름

1. **Controller** (`GET /posts/{id}/detail`) →
2. **CommunityService** →
3. `PostRepository.findById(id)` 으로 게시글 조회 →
4. `CommentRepository.findByPostOrderByCreatedAtDesc(post)` 으로 댓글 조회 →
5. 댓글 리스트를 `CommentResponseDto`로 변환 →
6. 게시글과 댓글을 합쳐 `PostDetailResponseDto` 생성 →
7. 클라이언트에 응답


### 📌 댓글 작성 흐름

1. **Controller** (`POST /posts/{id}/comments`) →
2. `CommentRequestDto` 로 요청 받음 →
3. **CommentService** 호출
4. `PostRepository.findById()` + `UserRepository.findById()` 로 참조 엔티티 조회
5. `Comment` 객체 생성 → 저장 → DTO로 응답


---

## 🧱 주요 코드 설명

### 📌 `Comment.java`
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "post_id", nullable = false)
private Post post;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;
```

### 📌 `CommentRepository.java`
```java
List<Comment> findByPostOrderByCreatedAtDesc(Post post);
```

### 📌 `CommentService.java`
```java
public CommentResponseDto createComment(Long postId, CommentRequestDto dto)
```

```java
public List<CommentResponseDto> getCommentsByPost(Long postId)
```

### 📌 `PostDetailResponseDto.java`
```java
@Getter
@Builder
public class PostDetailResponseDto {
    private Long id;
    private String content;
    private String imageUrl;
    private Long userId;
    private LocalDateTime createdAt;
    private List<CommentResponseDto> comments;
}
```

### 📌 `CommunityService.java`
```java
public PostDetailResponseDto getPostDetail(Long id) {
    Post post = postRepository.findById(id)...
    List<Comment> comments = commentRepository.findByPostOrderByCreatedAtDesc(post);
    List<CommentResponseDto> commentDtos = ...;

    return PostDetailResponseDto.builder()
        .id(post.getId())
        .content(post.getContent())
        .imageUrl(post.getImageUrl())
        .userId(post.getUserId())
        .createdAt(post.getCreatedAt())
        .comments(commentDtos)
        .build();
}
```

---

## 🧪 Postman 테스트 예시

### ✅ 댓글 작성
- Method: `POST`
- URL: `http://localhost:8080/api/community/posts/1/comments`
```json
{
  "userId": 1,
  "content": "댓글 기능 구현 완료!"
}
```

### ✅ 게시글 상세 조회 (댓글 포함)
- Method: `GET`
- URL: `http://localhost:8080/api/community/posts/1/detail`

```json
{
  "id": 1,
  "content": "야미야미",
  "imageUrl": "/uploads/xxx.jpg",
  "userId": 1,
  "createdAt": "2025-04-05T22:01:24",
  "comments": [
    {
      "id": 1,
      "userId": 1,
      "content": "댓글 기능 구현 완료!",
      "createdAt": "2025-04-05T23:00:22"
    }
  ]
}
```

---

## 🧠 배운 점 요약

- `@ManyToOne` 연관관계를 통해 객체 간 연결을 명확히 하고, 유지보수성과 확장성을 높일 수 있다.
- `DTO`를 사용하면 Entity를 직접 노출하지 않고 API 응답 구조를 깔끔하게 구성할 수 있다.
- `Builder 패턴`을 적극 활용해 객체 생성 가독성을 향상시킬 수 있다.
- 댓글 기능처럼 다대일 관계를 잘 설계해두면, 추후 "내가 쓴 댓글 보기", "댓글 좋아요", "댓글 신고" 등의 확장도 쉽다.
- 서비스 계층에서 비즈니스 로직을 집중시키고, 컨트롤러는 API 역할만 분리하는 구조가 유지보수에 유리하다.

---

✅ 이 문서는 커뮤니티 기능의 "기본이자 핵심"을 정리한 기준 문서로, 이후 확장 기능(좋아요, 신고, 사용자 댓글 조회 등)을 설계할 때 기초로 활용할 수 있다.