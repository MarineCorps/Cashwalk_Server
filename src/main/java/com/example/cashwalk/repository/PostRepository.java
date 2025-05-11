package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Post;
import com.example.cashwalk.entity.PostCategory;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    // ✅ 조회수 증가 쿼리
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.views = p.views + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);

    // ✅ 특정 postCategory로 조회 (ex. BESTLIVE 초기화용)
    List<Post> findByPostCategory(PostCategory category);

    // ✅ 게시글 작성자 기반 조회
    List<Post> findAllByUser(User user);

    // ✅ 게시글 + 작성자 조회 (DetailView에서 사용)
    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.id = :postId")
    Optional<Post> findWithUserById(@Param("postId") Long postId);
}
