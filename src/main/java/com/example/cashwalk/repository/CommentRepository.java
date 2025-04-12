/*✅ 역할
댓글 저장, 조회, 게시글 기준으로 댓글 목록 불러오기

JPA 메서드 이름 기반 쿼리로 작성자(user), 게시글(post) 기준으로 조회 가능*/
package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Comment;
import com.example.cashwalk.entity.Post;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

    int countByPost(Post post);
    int countByPostId(Long postId);

    // ✅ 내가 쓴 댓글 조회
    List<Comment> findAllByUser(User user);

    // 내가 작성한 댓글을 작성일 기준으로 내림차순으로 조회해줌
    List<Comment> findAllByUserOrderByCreatedAtDesc(User user);

    // 댓글 단 게시글 목록 조회를위해
    // 한 게시글에 여러개 댓글 여러개 달아도 한번만 특정사용자만 최신글 기준 정렬
    @Query("SELECT DISTINCT c.post FROM Comment c WHERE c.user.id = :userId ORDER BY c.post.createdAt DESC")
    List<Post> findDistinctPostsCommentedByUser(@Param("userId") Long userId);


    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId AND EXISTS (" +
            "SELECT r FROM Comment r WHERE r.parent.id = c.id)")
    List<Comment> findMyCommentsWithReplies(@Param("userId") Long userId);


}
/*📘 문법 설명

JpaRepository<Comment, Long>
Comment 엔티티에 대해 CRUD + 페이징 자동 지원

findByPostOrderByCreatedAtDesc()
post 엔티티를 기준으로 댓글을 조회하며, 최신순 정렬

Post post
연관관계로 선언된 필드명(post)을 기준으로 쿼리 생성됨

반환값이 List<Comment>
댓글은 페이징이 아니라 전체 목록을 리턴하도록 구현 (필요시 변경 가능)*/
