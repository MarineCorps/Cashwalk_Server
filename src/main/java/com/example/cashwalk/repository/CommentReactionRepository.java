package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Comment;
import com.example.cashwalk.entity.CommentReaction;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface CommentReactionRepository extends JpaRepository<CommentReaction,Long> {
    // 특정 유저가 특정 댓글에 남긴 반응이 있는지 없는지 조회
    Optional<CommentReaction> findByUserAndComment(User user, Comment comment);

    //추천/비추천 개수 조회용
    int countByCommentAndStatus(Comment comment, CommentReaction.Status statuc);

}
