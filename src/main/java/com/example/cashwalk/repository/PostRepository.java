package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Post;
import com.example.cashwalk.entity.BoardType;
import com.example.cashwalk.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    // ✅ 조회수 증가 쿼리 - 사용됨
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.views = p.views + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);


    // ✅ 실시간 베스트 / 명예의 전당 등에서 사용
    @Query("SELECT p FROM Post p WHERE p.createdAt >= :since")
    List<Post> findPostsCreatedAfter(@Param("since") LocalDateTime since);

    // ✅ 내가 작성한 글 목록 조회
    List<Post> findAllByUser(User user);
}
