package com.example.cashwalk.service;

import com.example.cashwalk.entity.BoardType;
import com.example.cashwalk.entity.FavoriteFilter;
import com.example.cashwalk.entity.PostCategory;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.FavoriteFilterRepository;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteFilterService {

    private final FavoriteFilterRepository favoriteFilterRepository;
    private final UserRepository userRepository;

    /**
     * 즐겨찾기 등록 (BoardType 또는 PostCategory)
     */
    @Transactional
    public void addFavorite(Long userId, BoardType boardType, PostCategory postCategory) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        // 게시판 즐겨찾기 추가
        if (boardType != null && !favoriteFilterRepository.existsByUserAndBoardType(user, boardType)) {
            FavoriteFilter favorite = FavoriteFilter.builder()
                    .user(user)
                    .boardType(boardType)
                    .createdAt(LocalDateTime.now()) // ✅ 필수!
                    .build();
            favoriteFilterRepository.save(favorite);
        }

        // 인기글 카테고리 즐겨찾기 추가
        if (postCategory != null && !favoriteFilterRepository.existsByUserAndPostCategory(user, postCategory)) {
            FavoriteFilter favorite = FavoriteFilter.builder()
                    .user(user)
                    .postCategory(postCategory)
                    .createdAt(LocalDateTime.now()) // ✅ 필수!
                    .build();
            favoriteFilterRepository.save(favorite);
        }
    }

    /**
     * 즐겨찾기 해제 (BoardType 또는 PostCategory)
     */
    @Transactional
    public void removeFavorite(Long userId, BoardType boardType, PostCategory postCategory) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        // 게시판 즐겨찾기 제거
        if (boardType != null) {
            favoriteFilterRepository.findByUserAndBoardType(user, boardType)
                    .ifPresent(favoriteFilterRepository::delete);
        }

        // 인기글 카테고리 즐겨찾기 제거
        if (postCategory != null) {
            favoriteFilterRepository.findByUserAndPostCategory(user, postCategory)
                    .ifPresent(favoriteFilterRepository::delete);
        }
    }

    /**
     * 즐겨찾기한 모든 필터 목록 반환 (BoardType + PostCategory 이름 문자열로 반환)
     */
    @Transactional(readOnly = true)
    public List<String> getMyFavoriteFilterKeys(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return favoriteFilterRepository.findByUser(user).stream()
                .map(filter -> {
                    if (filter.getBoardType() != null) {
                        return filter.getBoardType().name(); // ex: NOTICE
                    } else if (filter.getPostCategory() != null) {
                        return filter.getPostCategory().name(); // ex: BEST_HALL_OF_FAME
                    } else {
                        return "UNKNOWN";
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 즐겨찾기한 BoardType 목록 (게시판 필터용)
     */
    @Transactional(readOnly = true)
    public List<BoardType> getMyFavoriteBoardTypes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return favoriteFilterRepository.findByUser(user).stream()
                .map(FavoriteFilter::getBoardType)
                .filter(type -> type != null)
                .collect(Collectors.toList());
    }

    /**
     * 즐겨찾기한 PostCategory 목록 (인기글/명예의전당 필터용)
     */
    @Transactional(readOnly = true)
    public List<PostCategory> getMyFavoritePostCategories(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return favoriteFilterRepository.findByUser(user).stream()
                .map(FavoriteFilter::getPostCategory)
                .filter(type -> type != null)
                .collect(Collectors.toList());
    }
}
