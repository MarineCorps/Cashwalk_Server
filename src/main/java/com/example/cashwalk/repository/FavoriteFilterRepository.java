package com.example.cashwalk.repository;

import com.example.cashwalk.entity.BoardType;
import com.example.cashwalk.entity.FavoriteFilter;
import com.example.cashwalk.entity.PostCategory;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteFilterRepository extends JpaRepository<FavoriteFilter, Long> {

    List<FavoriteFilter> findByUser(User user);

    Optional<FavoriteFilter> findByUserAndBoardType(User user, BoardType boardType);

    Optional<FavoriteFilter> findByUserAndPostCategory(User user, PostCategory postCategory);

    boolean existsByUserAndBoardType(User user, BoardType boardType);

    boolean existsByUserAndPostCategory(User user, PostCategory postCategory);
}
