package com.example.cashwalk.repository;

import com.example.cashwalk.dto.RankingUserDto;

import java.time.LocalDate;
import java.util.List;

public interface RankingRepositoryCustom {
    List<RankingUserDto> getTodayRankingWithFriends(Long userId, LocalDate today);
}
