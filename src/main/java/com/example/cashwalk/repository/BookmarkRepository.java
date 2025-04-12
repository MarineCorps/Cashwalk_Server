package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Bookmark;
import com.example.cashwalk.entity.Post;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    //  특정 사용자가 특정 게시글을 북마크했는지 확인
    Optional<Bookmark> findByUserAndPost(User user, Post post);

    //  사용자의 북마크 전체 조회
    List<Bookmark> findAllByUser(User user);

    //  특정 게시글에 대한 모든 북마크 삭제 (게시글 삭제 시 cascade용)
    void deleteAllByPost(Post post);

    //  특정 사용자 기준 북마크 삭제 (회원 탈퇴 시 cascade용)
    void deleteAllByUser(User user);
}
