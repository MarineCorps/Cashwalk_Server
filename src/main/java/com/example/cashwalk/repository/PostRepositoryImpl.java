package com.example.cashwalk.repository;

import com.example.cashwalk.dto.PostSearchCondition;
import com.example.cashwalk.entity.Post;
import com.example.cashwalk.entity.QPost;
import com.example.cashwalk.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 차단 유저의 게시글 제외 + 검색 + 정렬 처리된 게시글 목록 조회
     */
    @Override
    public Page<Post> searchPostsExcludingBlockedUsers(PostSearchCondition condition, Pageable pageable, List<Long> blockedUserIds) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        // 🔍 검색 조건 처리
        BooleanBuilder builder = new BooleanBuilder();
        if (condition.getKeyword() != null && !condition.getKeyword().isBlank()) {
            String keyword = condition.getKeyword();
            builder.and(
                    post.title.containsIgnoreCase(keyword)
                            .or(post.content.containsIgnoreCase(keyword))
                            .or(post.user.nickname.containsIgnoreCase(keyword))
            );
        }

        // 🔍 게시판 타입 필터링 (optional)
        if (condition.getBoardType() != null) {
            builder.and(post.boardType.eq(condition.getBoardType()));
        }

        // 🚫 차단한 유저의 게시글 제외
        if (blockedUserIds != null && !blockedUserIds.isEmpty()) {
            builder.and(post.user.id.notIn(blockedUserIds));
        }

        // 🔃 정렬 기준 지정
        OrderSpecifier<?> order = getOrderSpecifier(condition.getSort());

        // 📦 실제 쿼리 실행
        JPAQuery<Post> query = queryFactory
                .selectFrom(post)
                .join(post.user, user).fetchJoin()
                .where(builder)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Post> results = query.fetch();

        // 전체 개수 쿼리
        long total = queryFactory
                .select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total == 0 ? 0 : total);
    }

    /**
     * 게시글 정렬 기준 지정 (좋아요순 / 댓글순 / 조회수순 / 최신순)
     */
    private OrderSpecifier<?> getOrderSpecifier(String sort) {
        QPost post = QPost.post;
        PathBuilder<Post> pathBuilder = new PathBuilder<>(Post.class, "post");

        return switch (sort) {
            case "like" -> post.likeCount.desc();
            case "comment" -> post.commentCount.desc();
            case "views" -> post.views.desc();
            default -> post.createdAt.desc(); // 최신순
        };
    }
}
