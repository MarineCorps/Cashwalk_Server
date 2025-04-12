package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Post;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface PostLikeRepository extends JpaRepository<PostLike,Long>{

    //특정 사용자와 게시글 조합으로  좋아요/비추천  조회
    Optional<PostLike> findByUserAndPost(User user, Post post);
    //findByUserAndPost	좋아요/비추천 상태가 존재하는지 확인
    // (존재 시 Optional<PostLike> 리턴)

    int countByPostIdAndStatus(Long postId, PostLike.Status status);


    int countByPostAndStatus(Post post, PostLike.Status status);

    @Modifying
    @Query("DELETE FROM PostLike pl WHERE pl.post.id = :postId AND pl.status = :status")
    void deleteAllByPostIdAndStatus(@Param("postId") Long postId, @Param("status") PostLike.Status status);

    @Query("SELECT pl FROM PostLike pl WHERE pl.user.id = :userId AND pl.post.id = :postId")
    Optional<PostLike> findByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

}
