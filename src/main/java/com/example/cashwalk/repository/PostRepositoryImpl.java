package com.example.cashwalk.repository;

import com.example.cashwalk.dto.PostSearchCondition;
import com.example.cashwalk.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 🔍 게시글 검색 (차단 유저 제외 + 정렬 + 필터)
     */
    @Override
    public Page<Post> searchPostsExcludingBlockedUsers(PostSearchCondition condition, Pageable pageable, List<Long> blockedUserIds) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        if (condition.getKeyword() != null && !condition.getKeyword().isBlank()) {
            String keyword = condition.getKeyword();
            builder.and(
                    post.title.containsIgnoreCase(keyword)
                            .or(post.content.containsIgnoreCase(keyword))
                            .or(post.user.nickname.containsIgnoreCase(keyword))
            );
        }

        if (condition.getBoardType() != null) {
            builder.and(post.boardType.eq(condition.getBoardType()));
        }

        if (condition.getPostCategory() != null) {
            builder.and(post.postCategory.eq(condition.getPostCategory()));
        }

        if (blockedUserIds != null && !blockedUserIds.isEmpty()) {
            builder.and(post.user.id.notIn(blockedUserIds));
        }

        JPAQuery<Post> query = queryFactory
                .selectFrom(post)
                .join(post.user, user).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // LEGEND는 점수 기반 정렬
        if (condition.getPostCategory() == PostCategory.LEGEND) {
            query.orderBy(Expressions.numberTemplate(Double.class,
                    "({0} * 6 + {1} * 3 + {2} * 1)",
                    post.bookmarkCount, post.commentCount, post.views
            ).desc());
        } else {
            query.orderBy(getOrderSpecifier(condition.getSort()));
        }

        List<Post> results = query.fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0L);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort) {
        QPost post = QPost.post;

        return switch (sort) {
            case "comment" -> post.commentCount.desc();
            case "views" -> post.views.desc();
            default -> post.createdAt.desc(); // 최신순
        };
    }

    /**
     * 🔥 실시간 인기글 (24시간 내 상위 10개)
     */
    @Override
    public List<Post> findTop10BestLivePostsByScore() {
        QPost post = QPost.post;
        QPostLike like = QPostLike.postLike;

        return queryFactory
                .select(post)
                .from(post)
                .leftJoin(like).on(like.post.eq(post).and(like.status.eq(PostLike.Status.LIKE)))
                .where(post.createdAt.goe(LocalDateTime.now().minusHours(24)))
                .groupBy(post)
                .orderBy(
                        Expressions.numberTemplate(Double.class,
                                "(COUNT({0}) * 5 + {1} * 3 + {2}) / POWER(TIMESTAMPDIFF(HOUR, {3}, NOW()) + 2, 1.5)",
                                like.id, post.commentCount, post.views, post.createdAt
                        ).desc()
                )
                .limit(10)
                .fetch();
    }

    /**
     * 👑 명예의 전당 후보 (점수 기준)
     */
    @Override
    public List<Post> findLegendCandidatesByScore(double threshold) {
        QPost post = QPost.post;
        QPostLike like = QPostLike.postLike;

        return queryFactory
                .select(post)
                .from(post)
                .leftJoin(like).on(like.post.eq(post).and(like.status.eq(PostLike.Status.LIKE)))
                .groupBy(post)
                .having(
                        Expressions.numberTemplate(Double.class,
                                "(COUNT({0}) * 6 + {1} * 3 + {2})",
                                like.id, post.commentCount, post.views
                        ).goe(threshold)
                )
                .fetch();
    }
}
