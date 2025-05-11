## ✅ 정리 날짜
2025-04-06

## ✅ 구현한 기능 요약
Spring Boot 기반 커뮤니티 기능 중 **댓글 작성 / 조회 / 수정 / 삭제** 기능을 JWT 기반 인증으로 완벽히 구현함.

---

## ✅ 구현 목표
- 댓글 작성자는 JWT로부터 추출된 userId를 기준으로 자동 설정
- 다른 사용자는 해당 댓글을 수정/삭제할 수 없도록 `403 Forbidden` 예외 처리
- DTO에는 userId를 받지 않음 (보안 강화를 위해 서버에서 추출)

---

## ✅ 디렉토리별 역할 요약
| 디렉토리 | 설명 |
|----------|------|
| `controller` | API 엔드포인트 (댓글 작성, 조회, 수정, 삭제) |
| `service` | 비즈니스 로직 처리 및 권한 체크 |
| `dto` | 요청/응답용 객체 정의 (userId는 포함 X) |
| `security` | JWT 인증 필터, 인증 사용자 정보 제공 (`CustomUserDetails`) |

---

## ✅ 수정/생성한 파일 목록
- `CommentRequestDto.java` ✅ 수정 (userId 제거)
- `CommentUpdateRequestDto.java` ✅ 수정 (userId 제거)
- `CommentResponseDto.java` ✅ 유지
- `CommentService.java` ✅ 수정 및 메서드 추가
- `CommentController.java` ✅ 수정 및 메서드 추가
- `CustomUserDetails.java` ✅ 유지 (getUserId 사용)

---

## ✅ 전체 실행 흐름 요약

### 댓글 작성 POST `/api/community/comments/post/{postId}`
1. 클라이언트는 JWT 토큰과 댓글 내용을 전송함
2. 컨트롤러에서 `@AuthenticationPrincipal`로 로그인된 userId 추출
3. 서비스에서 Post, User 엔티티 조회 후 Comment 저장
4. 저장된 Comment를 DTO로 변환하여 응답

### 댓글 수정 PUT `/api/community/comments/{commentId}`
1. JWT로부터 사용자 ID 추출
2. 댓글 ID로 댓글 조회 → 댓글 작성자 ID와 로그인 사용자 ID 비교
3. 동일할 경우 댓글 내용 수정
4. 응답으로 수정된 댓글 내용 반환

### 댓글 삭제 DELETE `/api/community/comments/{commentId}`
1. JWT로부터 사용자 ID 추출
2. 댓글 ID로 댓글 조회 → 작성자 ID 확인
3. 동일할 경우 삭제 수행
4. 응답: 204 No Content → 후에 메시지 포함 200 OK 방식도 가능

---

## ✅ 주요 코드 설명

### 🧩 Controller - 인증 사용자 정보 추출
```java
@AuthenticationPrincipal CustomUserDetails userDetails;
Long userId = userDetails.getUserId();
```

### 🧩 Service - 권한 체크
```java
if (!comment.getUser().getId().equals(userId)) {
    throw new AccessDeniedException("댓글 수정 권한이 없습니다.");
}
```

---

## ✅ Postman 테스트 결과 요약

| 테스트 케이스 | 결과 | 설명 |
|------------------|--------|------|
| 다른 사람이 쓴 댓글 수정 | ❌ 403 | JWT userId != 댓글 작성자 |
| 본인이 쓴 댓글 수정 | ✅ 200 | 수정 성공 |
| 댓글 삭제 (권한 없음) | ❌ 403 | 권한 없음 |
| 댓글 삭제 (본인) | ✅ 204 | 삭제 성공 |

---

## ✅ 학습한 점 요약
- JWT에서 사용자 ID를 추출해보는 방식과 `@AuthenticationPrincipal`의 활용법
- 댓글 작성, 수정, 삭제 시 보안적으로 안전한 구조 설계법
- 클라이언트에서 userId를 절대 넘기지 않도록 처리하는 보안 강화 방법
- Postman에서 인증 기반 테스트 시 발생 가능한 예외 대응법
- `403 Forbidden`의 의미와 처리 방식, `AccessDeniedException` 커스터마이징

---

## ✅ 다음 단계
- 게시글 좋아요 기능 구현 시에도 동일한 방식으로 사용자 인증 처리
- 커뮤니티 정리 마무리 후 `.pdf`로 내보내기 및 책처럼 정리된 저장용 문서 작성

> ✅ 커뮤니티 댓글 기능은 이제 완벽히 구현 완료되었고, 실서비스 수준으로도 활용 가능!