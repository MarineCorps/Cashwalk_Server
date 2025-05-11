package com.example.cashwalk.service;

import com.example.cashwalk.dto.RankingUserDto;
import com.example.cashwalk.repository.RankingRepository;
import com.example.cashwalk.repository.StepsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;

    public List<RankingUserDto> getTodayRanking(Long userId) {
        return rankingRepository.getTodayRankingWithFriends(userId, LocalDate.now());
    }
}

