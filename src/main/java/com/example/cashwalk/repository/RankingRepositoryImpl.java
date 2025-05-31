package com.example.cashwalk.repository;

import com.example.cashwalk.dto.RankingUserDto;
import com.example.cashwalk.entity.QInvite;
import com.example.cashwalk.entity.QSteps;
import com.example.cashwalk.entity.QUser;
import com.example.cashwalk.entity.QFriend;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RankingUserDto> getTodayRankingWithFriends(Long userId, LocalDate today) {
        QUser user = QUser.user;
        QFriend friend = QFriend.friend1; // ✅ 친구 기반으로 변경
        QSteps steps = QSteps.steps;

        return queryFactory
                .select(Projections.constructor(
                        RankingUserDto.class,
                        user.id,
                        user.nickname,
                        user.profileImage,
                        steps.stepCount.coalesce(0),
                        user.id.eq(userId)
                ))
                .from(user)
                .leftJoin(friend)
                .on(
                        (friend.user.id.eq(userId).and(friend.friend.id.eq(user.id)))
                                .or(friend.friend.id.eq(userId).and(friend.user.id.eq(user.id)))
                )
                .leftJoin(steps)
                .on(steps.user.id.eq(user.id)
                        .and(steps.date.eq(today)))
                .where(
                        friend.user.id.eq(userId)
                                .or(friend.friend.id.eq(userId))
                                .or(user.id.eq(userId))
                )
                .groupBy(user.id)
                .orderBy(steps.stepCount.coalesce(0).desc())
                .fetch();
    }


}
