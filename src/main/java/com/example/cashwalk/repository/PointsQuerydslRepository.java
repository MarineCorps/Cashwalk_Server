package com.example.cashwalk.repository;

import com.example.cashwalk.entity.User;
import java.time.LocalDateTime;

public interface PointsQuerydslRepository {
    int getTodayStepRewardSum(User user, LocalDateTime start, LocalDateTime end);
}
