package com.example.cashwalk.repository;

import com.example.cashwalk.dto.PostSearchCondition;
import com.example.cashwalk.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {
    //차단한 유저의 게시글을 제외하고 검색
    Page<Post> searchPostsExcludingBlockedUsers(PostSearchCondition condition, Pageable pageable, List<Long> blockedUserIds);

}
