
# 📌 커뮤니티 게시글 상세조회 + 조회수 중복 방지 기능 정리

- **날짜**: 2025-04-11
- **기능 요약**: 게시글 상세 조회 시 Redis 기반 중복 방지 조회수 증가 로직 적용 + ViewCountService로 관심사 분리
- **구현 목표**: Redis + Spring AOP 기반으로 중복 없이 조회수 1회 증가, QueryDSL 기반 게시글 정렬 응답 확인

---

## 🧱 디렉토리 구조 및 역할 요약

| 디렉토리 | 설명 |
|----------|------|
| `service` | `CommunityService`, `ViewCountService`로 관심사 분리 |
| `repository` | `PostRepository` - 조회수 증가용 update 쿼리 포함 |
| `controller` | `CommunityController`에서 상세 API 호출 |
| `dto` | `PostDetailResponseDto`, `CommentResponseDto` 응답 구조 |
| `security` | `CustomUserDetails`로 로그인 사용자 ID 조회 |
| `utils` | `RedisService` 조회 기록 저장/확인 |

---

## 🗂️ 수정 / 생성 파일 목록

| 파일 | 수정 내용 |
|------|----------|
| `CommunityService.java` | 기존 `increaseViewCountIfNotDuplicate()` 삭제, `viewCountService.increaseIfNotDuplicate()` 호출로 대체 |
| `ViewCountService.java` | ✅ **신규 생성**, 조회수 증가 전담 |
| `PostRepository.java` | `@Modifying` + `@Query` 기반 `incrementViewCount()` 추가 |
| `CommunityController.java` | `getPostDetail()` 내 조회수 증가 호출 → ViewCountService 호출로 변경 |
| `PostDetailResponseDto.java` | `title` 필드 등 전체 게시글 정보 담기 위해 필드 보강 |
| `CustomUserDetails.java` | `getUserId()` 추가 (조회용) |
| `SecurityConfig.java` | `/api/community/posts/*/detail` 경로에 인증 적용 확인 |

---

## 🔄 전체 실행 흐름

1. 사용자가 `/api/community/posts/{id}/detail` 호출 (로그인 필요)
2. `CommunityController` → `getPostDetail()` 실행
3. `CommunityService.getPostDetail()` 진입
4. `viewCountService.increaseIfNotDuplicate(userId, postId)` 호출 → Redis 중복 조회 체크
5. Redis에 조회 기록이 없으면 → DB 조회수 증가 + Redis 저장
6. 게시글 상세 데이터 + 댓글 목록 + 좋아요 여부 조합 → `PostDetailResponseDto`로 응답

---

## 🔐 핵심 코드 요약

### 🔸 CommunityService.java (일부)
```java
viewCountService.increaseIfNotDuplicate(currentUserId, postId);
```

### 🔸 ViewCountService.java
```java
@Transactional
public void increaseIfNotDuplicate(Long userId, Long postId) {
    if (!redisService.hasViewPost(userId, postId)) {
        postRepository.incrementViewCount(postId);
        redisService.markPostAsViewed(userId, postId);
    }
}
```

### 🔸 PostRepository.java
```java
@Modifying
@Query("UPDATE Post p SET p.views = p.views + 1 WHERE p.id = :postId")
void incrementViewCount(@Param("postId") Long postId);
```

### 🔸 CustomUserDetails.java
```java
public Long getUserId() {
    return user.getId();
}
```

---

## ✅ 테스트 결과

| 항목 | 결과 |
|------|------|
| 첫 요청 | 조회수 1 증가 |
| 동일 유저 재요청 | 조회수 증가 ❌ (중복 방지 작동) |
| 다른 유저 요청 | 조회수 또 증가 ✅ |
| 응답 필드 | title, content, nickname, comments, views, likedByMe 등 포함 |

📸 Postman 결과 캡처:
- 조회수: 정상 증가 확인
- 댓글 목록: DTO로 정상 반환됨

---

## 📌 학습한 점 요약

- Spring에서 `@Transactional`은 같은 클래스 내부 메서드 간 호출엔 적용 안 됨 → **분리 필요**
- Redis TTL 기반으로 조회수 중복 방지 구현 가능
- ViewCountService로의 관심사 분리는 **트랜잭션 안정성 확보 + 유지보수 용이**
- Postman으로 API 요청 시 헤더에 `Authorization` 포함 여부 꼭 확인

---

✅ 정리 완료. 다음 기능으로 넘어가기 전, 정리본 `.md` 파일로 저장 가능
