package com.example.cashwalk.controller;

import com.example.cashwalk.dto.PostDetailResponseDto;
import com.example.cashwalk.dto.PostRequestDto;
import com.example.cashwalk.dto.PostResponseDto;
import com.example.cashwalk.entity.BoardType;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    // 게시글 작성
    @PostMapping("/posts")
    public ResponseEntity<PostResponseDto> createPost(
            @RequestPart("post") PostRequestDto requestDto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        requestDto.setUserId(userDetails.getUserId());
        PostResponseDto response = communityService.createPost(requestDto, imageFile);
        return ResponseEntity.ok(response);
    }

    // 게시글 목록 조회 (정렬 및 페이지네이션 지원)
    @GetMapping("/posts")
    public ResponseEntity<Page<PostResponseDto>> getPostList(
            @RequestParam(value = "boardType", required = false) BoardType boardType,
            @RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<PostResponseDto> posts = communityService.getPostList(boardType, sort, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        // 조회수 중복 방지 로직 적용
        communityService.increaseViewCountIfNotDuplicate(userId, id);

        // 게시글 데이터 조회
        PostResponseDto post = communityService.getPostById(id);
        return ResponseEntity.ok(post);
    }


    // 게시글 수정
    @PutMapping("/posts/{id}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long id,
            @RequestParam("content") String content,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        PostResponseDto updated = communityService.updatePost(id, content, imageFile);
        return ResponseEntity.ok(updated);
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        communityService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // 게시글 상세 + 댓글 조회
    @GetMapping("/posts/{id}/detail")
    public ResponseEntity<PostDetailResponseDto> getPostDetail(@PathVariable Long id) {
        PostDetailResponseDto postDetail = communityService.getPostDetail(id);
        return ResponseEntity.ok(postDetail);
    }

    // 게시글 추천
    @PostMapping("/posts/{id}/like")
    public ResponseEntity<Map<String, String>> likePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        communityService.likePost(id, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "추천");
        return ResponseEntity.ok(response);
    }

    // 게시글 비추천
    @PostMapping("/posts/{id}/dislike")
    public ResponseEntity<Map<String, String>> dislikePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        communityService.dislikePost(id, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "비추천");
        return ResponseEntity.ok(response);
    }
}
