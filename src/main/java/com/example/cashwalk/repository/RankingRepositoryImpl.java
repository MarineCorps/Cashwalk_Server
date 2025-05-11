package com.example.cashwalk.repository;

import com.example.cashwalk.dto.RankingUserDto;
import com.example.cashwalk.entity.QInvite;
import com.example.cashwalk.entity.QSteps;
import com.example.cashwalk.entity.QUser;
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
        QInvite invite = QInvite.invite;
        QSteps steps = QSteps.steps;

        return queryFactory
                .select(Projections.constructor(
                        RankingUserDto.class,
                        user.id,
                        user.nickname,
                        user.profileImage,
                        steps.stepCount.coalesce(0),   // ✅ 수정!
                        user.id.eq(userId)
                ))
                .from(user)
                .leftJoin(invite)
                .on(invite.invitee.id.eq(user.id)
                        .or(invite.referrer.id.eq(user.id)))
                .leftJoin(steps)
                .on(steps.user.id.eq(user.id)
                        .and(steps.date.eq(today)))
                .where(
                        invite.referrer.id.eq(userId)
                                .or(invite.invitee.id.eq(userId))
                                .or(user.id.eq(userId))
                )
                .groupBy(user.id)
                .orderBy(steps.stepCount.coalesce(0).desc())  // ✅ 수정!
                .fetch();

    }

}
