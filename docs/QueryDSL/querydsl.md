📌 방법 2. 검색 + 정렬을 같이 적용하려면 JPQL 동적 쿼리 or QueryDSL 필요
Keyword를 이용한 검색을 하려고했는데 오류가나서
검색+정렬을 한번에 할 수있는 querydsl을 사용하기로 한거임

github에 커밋하는 문제 해결해야됨

✅ 장기적이고 실무에서 가장 많이 쓰는 방법: QueryDSL 기반 동적 쿼리 처리
대기업/스타트업 포함 실무에서 “검색 + 정렬 + 필터”가 복합적으로 필요한 경우 거의 무조건 사용합니다.

✅ 왜 QueryDSL인가?
기능	설명
✅ 동적 쿼리	조건이 있을 때만 where절 생성 (keyword, sort, boardType 등)
✅ 정렬 동적 처리	orderBy()를 파라미터에 따라 적용 가능
✅ 타입 안전성	컴파일 시점에 쿼리 오류 검출 가능 (JPA 문자열 쿼리보다 안전)
✅ 유지보수	조건 추가/삭제 시 코드 수정이 쉬움
✅ 구현 예시 흐름
1. QueryDsl 설정 + 의존성 추가
   groovy
   복사
   // build.gradle
   implementation "com.querydsl:querydsl-jpa"
   annotationProcessor "com.querydsl:querydsl-apt"
   그리고 다음 플러그인 적용 (IntelliJ 기준):

groovy
복사
plugins {
id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"
}
2. PostRepositoryCustom + PostRepositoryImpl 생성
   java
   복사
   public interface PostRepositoryCustom {
   Page<Post> searchWithConditions(String keyword, String sort, BoardType boardType, Pageable pageable);
   }
   java
   복사
   public class PostRepositoryImpl implements PostRepositoryCustom {

   @PersistenceContext
   private EntityManager em;

   @Override
   public Page<Post> searchWithConditions(String keyword, String sort, BoardType boardType, Pageable pageable) {
   QPost post = QPost.post;

        JPAQuery<Post> query = new JPAQuery<>(em);
        BooleanBuilder builder = new BooleanBuilder();

        // 🔍 검색 조건
        if (keyword != null && !keyword.isBlank()) {
            builder.and(
                post.title.containsIgnoreCase(keyword)
                    .or(post.content.containsIgnoreCase(keyword))
            );
        }

        // 📌 게시판 필터
        if (boardType != null) {
            builder.and(post.boardType.eq(boardType));
        }

        // 🔀 정렬
        OrderSpecifier<?> orderSpecifier;
        switch (sort) {
            case "likes" -> orderSpecifier = post.likeCount.desc();
            case "comments" -> orderSpecifier = post.commentCount.desc();
            case "views" -> orderSpecifier = post.views.desc();
            default -> orderSpecifier = post.createdAt.desc();
        }

        List<Post> content = query
                .from(post)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = query
                .select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
   }
   }
   ✅ BooleanBuilder: 동적 조건 쿼리 구성
   ✅ orderSpecifier: 정렬 기준을 동적으로 선택
   ✅ offset/limit: 페이지네이션 처리
   ✅ PageImpl<>: JPA Page 반환

3. 기존 서비스 클래스에서 이 쿼리 호출
   java
   복사
   @Autowired
   private PostRepository postRepository; // QueryDSL 확장된 인터페이스

public Page<PostResponseDto> getPostList(BoardType boardType, String sort, Pageable pageable, String keyword) {
Page<Post> posts = postRepository.searchWithConditions(keyword, sort, boardType, pageable);

    return posts.map(post -> {
        String nickname = userRepository.findById(post.getUserId())
                .map(User::getNickname)
                .orElse("알 수 없음");

        int likeCount = postLikeRepository.countByPostIdAndStatus(post.getId(), PostLike.Status.LIKE);
        int commentCount = commentRepository.countByPostId(post.getId());

        return PostResponseDto.from(post, nickname, likeCount, commentCount);
    });
}
✅ 장점 요약
항목	내용
검색 + 정렬 동시 지원	keyword + sort 조합 자유롭게 가능
유지보수 용이	조건 추가/삭제 쉬움
성능 최적화 가능	JPQL 직접 튜닝도 가능
타입 안전성	컴파일 단계에서 오류 잡힘
✅ 마무리
✅ 지금부터는 검색 + 정렬 + 게시판 타입까지 유연하게 필터링 가능한 강력한 커뮤니티 조회 API를 갖추게 됩니다.

📦 최종 구조 요약 (디렉토리 예시)
복사
📂 repository
├── PostRepository.java                ← 기존 + 확장 인터페이스
├── PostRepositoryCustom.java         ← QueryDSL 정의용 인터페이스
└── PostRepositoryImpl.java           ← QueryDSL 구현체

이제 본격적으로 QueryDSL 기반의 게시글 검색 + 정렬 기능을 리팩토링해봅시다.
지금부터는 실무에서 사용하는 방식 그대로, QueryDSL을 완벽히 설정하고 구현하겠습니다.

✅ 전체 작업 순서 (단계별로 안내)
단계	작업 내용	파일/영역
1️⃣	QueryDSL 의존성 추가	build.gradle
2️⃣	Q클래스 자동 생성 설정	gradle plugins
3️⃣	PostRepositoryCustom 생성	interface
4️⃣	PostRepositoryImpl 생성	QueryDSL 로직 구현
5️⃣	PostRepository 확장	extends PostRepositoryCustom
6️⃣	CommunityService에서 searchWithConditions() 사용
7️⃣	테스트 & Flutter 연동 확인


✅ 2단계: PostRepositoryCustom 생성
java
복사
package com.example.cashwalk.repository;

import com.example.cashwalk.entity.BoardType;
import com.example.cashwalk.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
Page<Post> searchWithConditions(String keyword, String sort, BoardType boardType, Pageable pageable);
}
✅ 3단계: PostRepositoryImpl 생성
java
복사
package com.example.cashwalk.repository;

import com.example.cashwalk.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> searchWithConditions(String keyword, String sort, BoardType boardType, Pageable pageable) {
        QPost post = QPost.post;
        BooleanBuilder where = new BooleanBuilder();

        if (keyword != null && !keyword.isBlank()) {
            where.and(post.title.containsIgnoreCase(keyword)
                    .or(post.content.containsIgnoreCase(keyword)));
        }

        if (boardType != null) {
            where.and(post.boardType.eq(boardType));
        }

        OrderSpecifier<?> order;
        switch (sort) {
            case "likes" -> order = post.likeCount.desc();
            case "comments" -> order = post.commentCount.desc();
            case "views" -> order = post.views.desc();
            default -> order = post.createdAt.desc();
        }

        List<Post> content = queryFactory
                .selectFrom(post)
                .where(where)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(post.count())
                .from(post)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
✅ queryFactory는 생성자 주입
✅ BooleanBuilder는 동적 where
✅ PageImpl<>로 반환

✅ 4단계: PostRepository 인터페이스 확장
java
복사
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
// 기존 메서드도 그대로 유지
}
✅ 5단계: CommunityService.java 수정
java
복사
public Page<PostResponseDto> getPostList(BoardType boardType, String sort, Pageable pageable, String keyword) {
Page<Post> posts = postRepository.searchWithConditions(keyword, sort, boardType, pageable);

    return posts.map(post -> {
        String nickname = userRepository.findById(post.getUserId())
                .map(User::getNickname)
                .orElse("알 수 없음");
        int likeCount = postLikeRepository.countByPostIdAndStatus(post.getId(), PostLike.Status.LIKE);
        int commentCount = commentRepository.countByPostId(post.getId());
        return PostResponseDto.from(post, nickname, likeCount, commentCount);
    });
}
✅ 6단계: QueryDslConfig (선택 사항)
java
복사
@Configuration
public class QueryDslConfig {
@PersistenceContext
private EntityManager em;

    @Bean
    public JPAQueryFactory queryFactory() {
        return new JPAQueryFactory(em);
    }
}