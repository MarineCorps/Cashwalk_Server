package com.example.cashwalk.repository;
import com.example.cashwalk.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    //게시글을 최신순으로 페이징 조회
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

/*📘 Java/Spring 문법 설명
문법	설명
extends JpaRepository<Post, Long>	Post 엔티티에 대해 CRUD 기능을 자동 제공. ID는 Long 타입
Pageable	스프링에서 제공하는 페이징 처리 인터페이스 (page, size 파라미터 자동 처리)
Page<Post>	페이징된 게시글 리스트
findAllByOrderByCreatedAtDesc	createdAt 필드 기준으로 내림차순 정렬하여 전체 조회*/
