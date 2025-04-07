package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Post;
import com.example.cashwalk.entity.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    // [1] 좋아요 수 기준 정렬
    @Query(value = """
        SELECT p.id, p.title, p.content, p.image_url, p.user_id, p.created_at,
               u.nickname,
               (SELECT COUNT(*) FROM post_likes pl WHERE pl.post_id = p.id AND pl.status = 'LIKE') AS like_count,
               (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) AS comment_count,p.views
        FROM post p
        JOIN user u ON p.user_id = u.id
        WHERE (:boardType IS NULL OR p.board_type = :boardType)
        ORDER BY like_count DESC, p.created_at DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM post p
        WHERE (:boardType IS NULL OR p.board_type = :boardType)
        """,
            nativeQuery = true)
    Page<Object[]> findAllOrderByLikes(@Param("boardType") String boardType, Pageable pageable);


    // [2] 댓글 수 기준 정렬
    @Query(value = """
        SELECT p.id, p.title, p.content, p.image_url, p.user_id, p.created_at,
               u.nickname,
               (SELECT COUNT(*) FROM post_likes pl WHERE pl.post_id = p.id AND pl.status = 'LIKE') AS like_count,
               (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) AS comment_count,p.views
        FROM post p
        JOIN user u ON p.user_id = u.id
        WHERE (:boardType IS NULL OR p.board_type = :boardType)
        ORDER BY comment_count DESC, p.created_at DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM post p
        WHERE (:boardType IS NULL OR p.board_type = :boardType)
        """,
            nativeQuery = true)
    Page<Object[]> findAllOrderByCommentCount(@Param("boardType") String boardType, Pageable pageable);
    // [3] 조회수 별 정렬
    @Query(value = """
    SELECT p.id, p.title, p.content, p.image_url, p.user_id, p.created_at,
           u.nickname,
           (SELECT COUNT(*) FROM post_likes pl WHERE pl.post_id = p.id AND pl.status = 'LIKE') AS like_count,
           (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) AS comment_count, p.views
    FROM post p
    JOIN user u ON p.user_id = u.id
    WHERE (:boardType IS NULL OR p.board_type = :boardType)
    ORDER BY p.views DESC, p.created_at DESC
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM post p
    WHERE (:boardType IS NULL OR p.board_type = :boardType)
    """,
            nativeQuery = true)
    Page<Object[]> findAllOrderByViews(@Param("boardType") String boardType, Pageable pageable);


    // 조회수 증가 쿼리
    @Modifying
    @Query("UPDATE Post p SET p.views = p.views + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);


    // [4] 최신순 정렬
    Page<Post> findAllByBoardTypeOrderByCreatedAtDesc(BoardType boardType, Pageable pageable);
}
