# 📈 커뮤니티 게시글 조회수 중복 방지 기능 (Redis 기반)

## 🗓️ 구현 일자
2025-04-07

---

## ✅ 기능 요약

사용자가 커뮤니티 게시글을 조회할 때마다 조회수를 무조건 증가시키는 것이 아니라,  
**동일 사용자가 같은 게시글을 1시간 이내에 다시 조회하면 조회수가 오르지 않도록** 설계했다.

이를 위해 Redis를 사용하여 `(userId, postId)`를 키로 조회 기록을 저장하고,  
조회 기록이 존재하지 않을 경우에만 조회수를 증가시킨다.

---

## 🎯 구현 목표

- 게시글 조회 시 1시간 내 중복 조회는 무시
- Redis TTL 기능을 이용한 시간 제한
- Redis 키는 `"viewed:userId:postId"` 형식으로 저장
- 조회수 증가 책임은 단일 메서드에만 위임

---

## 📁 디렉토리별 주요 수정 파일

| 디렉토리 | 파일명 | 설명 |
|----------|--------|------|
| `config` | `RedisConfig.java` | Redis 연결 및 RedisTemplate 설정 |
| `service` | `RedisService.java` | Redis 저장 및 중복 조회 확인 로직 |
| `service` | `CommunityService.java` | 조회수 증가 로직 및 게시글 상세조회 |
| `controller` | `CommunityController.java` | 게시글 조회 API 수정 |
| `dto` | `PostResponseDto.java` | 조회수 필드 포함 DTO |

---

## 🔄 전체 동작 흐름

1. 사용자가 `/api/community/posts/{id}` 로 게시글 조회
2. `CommunityController`에서 userId, postId 기반 조회수 증가 메서드 호출
3. `RedisService`에서 해당 유저가 1시간 내에 이 게시글을 봤는지 확인
4. 조회 기록이 없으면 DB에서 조회수 1 증가 + Redis에 조회 기록 저장
5. 게시글 상세 데이터 반환 (조회수 증가 여부와 관계없이)

---

## 🔧 주요 코드 설명

### 📌 RedisConfig.java

```java
@Bean
public RedisTemplate<String, String> redisTemplate() {
    RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());
    return redisTemplate;
}
```

→ Redis에 String 기반 key-value 저장을 위한 설정

---

### 📌 RedisService.java

```java
public boolean hasViewPost(Long userId, Long postId) {
    String key = generateKey(userId, postId);
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
}

public void markPostAsViewed(Long userId, Long postId) {
    String key = generateKey(userId, postId);
    redisTemplate.opsForValue().set(key, "viewed", 1, TimeUnit.HOURS);
}
```

→ Redis에 조회 기록 저장 및 TTL 1시간 설정

---

### 📌 CommunityService.java

```java
@Transactional
public void increaseViewCountIfNotDuplicate(Long userId, Long postId) {
    if (!redisService.hasViewPost(userId, postId)) {
        postRepository.incrementViewCount(postId);
        redisService.markPostAsViewed(userId, postId);
    }
}
```

→ 조회 기록이 없을 때만 조회수 증가

**❗ getPostById에서는 조회수 증가 코드 제거됨**

---

### 📌 CommunityController.java

```java
@GetMapping("/posts/{id}")
public ResponseEntity<PostResponseDto> getPost(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails.getUserId();
    communityService.increaseViewCountIfNotDuplicate(userId, id); // ✅ 순서 주의!
    PostResponseDto post = communityService.getPostById(id);
    return ResponseEntity.ok(post);
}
```

→ 컨트롤러에서 조회수 증가 메서드 호출

---

## 🧪 테스트 결과

- 동일 사용자가 게시글 1번 클릭 → 조회수 +1
- 1시간 이내 재클릭 → 조회수 변동 없음
- 다른 사용자 → 조회수 +1
- 1시간 경과 후 같은 사용자 → 조회수 다시 +1

---

## ✨ 배운 점 정리

- Redis TTL을 활용하면 세션 기반 행동 제어가 간단해진다.
- 컨트롤러에서 메서드 호출 순서와 파라미터는 꼭 확인해야 한다.
- 단일 책임 원칙(SRP)을 적용해 `조회수 증가`는 별도 메서드에서만 수행하도록 분리하는 것이 유지보수에 유리하다.

---