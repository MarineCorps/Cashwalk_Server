# ✅ 게시글 검색 + 정렬 기능 구현 (with QueryDSL)

### 📅 날짜: 2025-04-11

---

## 🧩 기능 요약
- 게시글 검색 (키워드 기반: 제목, 내용, 작성자 닉네임 포함)
- 게시글 정렬 (기본 최신순, 좋아요순, 댓글순)
- QueryDSL을 활용한 동적 검색 + 정렬 + 페이징
- PostResponseDto로 응답 데이터 안전하게 반환

---

## 🎯 구현 목표
- 검색: 사용자가 입력한 키워드가 제목, 내용, 작성자 닉네임에 포함된 게시글 조회
- 정렬: 최신순, 좋아요순, 댓글순 기준 동적 정렬
- 페이징: Spring Pageable 기반으로 구현
- 보안: password, pointHistory 등 민감한 정보 제외

---

## 🗂️ 디렉토리별 역할 요약

| 디렉토리 | 설명 |
|----------|------|
| `controller` | 검색 API 호출 엔드포인트 (`/api/community/search`) |
| `service` | PostRepository로부터 검색 결과 받아 DTO로 변환 |
| `repository` | QueryDSL 기반 동적 검색/정렬 로직 구현 |
| `dto` | 검색 조건 전달용 DTO, 응답용 PostResponseDto |
| `entity` | Post, User 관계 + 정렬 필드(likeCount, commentCount 등) |
| `config` | QuerydslConfig: JPAQueryFactory Bean 등록 |

---

## 📝 수정/생성한 파일 목록

### 📁 `dto`
- `PostSearchCondition.java` ✅ 검색 조건 DTO 생성
- `PostResponseDto.java` (기존) ➕ `from(Post, nickname, like, comment)` 메서드 활용

### 📁 `repository`
- `PostRepositoryCustom.java` ✅ searchPosts 메서드 정의
- `PostRepositoryImpl.java` ✅ QueryDSL 로직 구현
- `PostRepository.java`
  ```java
  // ✅ 다음 메서드들은 QueryDSL로 대체되어 주석처리하거나 삭제 가능
  // Page<Object[]> findAllOrderByLikes(...);
  // Page<Object[]> findAllOrderByCommentCount(...);
  // Page<Object[]> findAllOrderByViews(...);
  // Page<Post> searchByKeyword(...);
  // Page<Post> findAllByBoardTypeOrderByCreatedAtDesc(...);
  ```

### 📁 `service`
- `CommunityService.java`
  ```java
  // ✅ getPostList(...) 메서드는 searchPosts()로 대체 가능하므로 주석처리 검토
  // public Page<PostResponseDto> getPostList(...) { ... }
  ```

### 📁 `controller`
- `CommunityController.java`
  ```java
  // ✅ getPostList()를 호출하는 /posts API는 /search로 통합 가능
  // @GetMapping("/posts")
  // public ResponseEntity<Page<PostResponseDto>> getPostList(...) { ... }
  ```

### 📁 `config`
- `QuerydslConfig.java` ✅ `JPAQueryFactory` Bean 등록
- `SecurityConfig.java` 🔧 `/api/community/search` 허용 확인 (문제 없음)

### 📁 `entity`
- `Post.java` 🔧 `User`, `likeCount`, `commentCount`, `views` 필드 추가

---

## 🔄 전체 실행 흐름

1. Flutter에서 `/api/community/search?keyword=xx&sort=like` 요청
2. `CommunityController`에서 `PostSearchCondition` 파라미터 매핑
3. `CommunityService`에서 검색 조건을 전달해 `PostRepository.searchPosts()` 호출
4. `PostRepositoryImpl`에서 `BooleanBuilder` + `orderBy()`로 동적 쿼리 실행
5. 검색된 `Post` 리스트를 `PostResponseDto`로 변환하여 응답

---

## 🔎 주요 코드 설명

### ✅ PostRepositoryImpl.java
```java
BooleanBuilder builder = new BooleanBuilder();
if (condition.getKeyword() != null) {
    builder.and(
        post.title.containsIgnoreCase(keyword)
            .or(post.content.containsIgnoreCase(keyword))
            .or(post.user.nickname.containsIgnoreCase(keyword))
    );
}

OrderSpecifier<?> order = switch (condition.getSort()) {
    case "like" -> post.likeCount.desc();
    case "comment" -> post.commentCount.desc();
    default -> post.createdAt.desc();
};
```

### ✅ CommunityService.java
```java
return postRepository.searchPosts(condition, pageRequest)
        .map(post -> PostResponseDto.from(
            post,
            post.getUser().getNickname(),
            post.getLikeCount(),
            post.getCommentCount()
        ));
```

---

## ✅ 테스트 결과 요약
- ✅ 검색 정상 작동 (제목/내용/닉네임 포함 여부 확인)
- ✅ 정렬 정상 작동 (like / comment / latest)
- ✅ 페이징 동작 확인 (`totalPages`, `pageNumber`, `size` 등 포함)
- ✅ 응답 데이터 최소화 (User 내부 정보 제거됨)

---

## 💡 학습한 점 요약 (QueryDSL)

### 📌 QueryDSL이란?
> QueryDSL은 SQL/JPQL을 자바 코드처럼 타입 안정성 있게 작성할 수 있도록 도와주는 라이브러리다.

- 문자열 기반 JPQL → Java 기반 쿼리로 전환
- 컴파일 타임에 필드 체크 가능 → 런타임 오류 방지
- 복잡한 조건 조합, 페이징, 정렬을 쉽게 처리 가능

### 📌 QClass가 필요한 이유?
- Entity 기반으로 생성되는 쿼리 도우미 클래스
- `QPost.post.title` 처럼 자바 코드로 안전하게 쿼리 작성 가능

### 📌 QueryDSL 기본 사용 구조
```java
QPost post = QPost.post;

queryFactory
    .selectFrom(post)
    .where(post.title.contains("운동"))
    .orderBy(post.likeCount.desc())
    .offset(0)
    .limit(10)
    .fetch();
```

---

### ✅ 장점
- 타입 안정성 (컴파일 오류로 확인 가능)
- 동적 쿼리 편리 (BooleanBuilder, 조건부 where)
- 복잡한 쿼리 가독성 향상

### ❌ 단점
- 설정 복잡 (annotationProcessor, Q파일 생성)
- 코드 양 증가, IDE 의존도 높음

---

✅ **검색 + 정렬 + DTO 응답까지 완전 구성 완료**
> 이제 이 구조 기반으로 인기글, 유저글 조회 등 다양한 확장이 가능함! 💪

