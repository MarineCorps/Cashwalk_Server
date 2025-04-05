/*✅ 역할
댓글 저장, 조회, 게시글 기준으로 댓글 목록 불러오기

JPA 메서드 이름 기반 쿼리로 작성자(user), 게시글(post) 기준으로 조회 가능*/
package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Comment;
import com.example.cashwalk.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostOrderByCreatedAtDesc(Post post);
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
