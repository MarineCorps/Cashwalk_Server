package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Points;
import com.example.cashwalk.entity.PointsType;
import com.example.cashwalk.entity.QPoints;
import com.example.cashwalk.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class PointsQuerydslRepositoryImpl implements PointsQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public int getTodayStepRewardSum(User user, LocalDateTime start, LocalDateTime end) {
        QPoints p = QPoints.points;

        Integer sum = queryFactory
                .select(p.amount.sum())
                .from(p)
                .where(
                        p.user.eq(user),
                        p.type.eq(PointsType.STEP_REWARD),
                        p.createdAt.between(start, end)
                )
                .fetchOne();

        return sum != null ? sum : 0;
    }
}
