package com.example.cashwalk.controller;
// import 추가
import com.example.cashwalk.entity.PostCategory;
import com.example.cashwalk.service.CommentService;
import com.example.cashwalk.dto.CommentResponseDto;
import com.example.cashwalk.dto.PostResponseDto;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.CommunityService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import com.example.cashwalk.service.BookmarkService;
import java.util.Map;

import com.example.cashwalk.dto.*;
import com.example.cashwalk.entity.BoardType;
import com.example.cashwalk.entity.Post;
import com.example.cashwalk.service.CommunityService;
import com.example.cashwalk.service.ViewCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final ViewCountService viewCountService;
    private final BookmarkService bookmarkService;
    private final CommentService commentService;
    // ✅ 검색 + 정렬 API
    @GetMapping("/search")
    public Page<PostResponseDto> searchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String boardType,
            @RequestParam(required = false) String postCategory,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PostSearchCondition condition = new PostSearchCondition();
        condition.setKeyword(keyword);
        condition.setSort(sort);
        condition.setPage(page);
        condition.setSize(size);

        // ✅ boardType 파싱
        if (boardType != null) {
            try {
                condition.setBoardType(BoardType.valueOf(boardType));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid boardType value: {}", boardType);
            }
        }

        // ✅ postCategory 파싱 (여기!)
        if (postCategory != null) {
            try {
                condition.setPostCategory(PostCategory.valueOf(postCategory));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid postCategory value: {}", postCategory);
            }
        }

        Long currentUserId = userDetails.getUserId();
        return communityService.searchPosts(condition, currentUserId);
    }




    // 게시글 작성
    @PostMapping("/posts")
    public ResponseEntity<PostResponseDto> createPost(
            @RequestPart("post") PostRequestDto requestDto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("💬 userId from JWT: {}", userDetails.getUserId());
        log.info("💬 userId in requestDto BEFORE set: {}", requestDto.getUserId());
        requestDto.setUserId(userDetails.getUserId());
        log.info("💬 userId in requestDto AFTER set: {}", requestDto.getUserId());
        PostResponseDto response = communityService.createPost(requestDto, imageFile);
        return ResponseEntity.ok(response);
    }

    //조회수 중복 방지
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        // 조회수 중복 방지 로직 적용
        viewCountService.increaseIfNotDuplicate(userId, id);

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
    public ResponseEntity<PostDetailResponseDto> getPostDetail(
            @PathVariable("id") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long currentUserId = userDetails.getUserId();
        PostDetailResponseDto response = communityService.getPostDetail(postId, currentUserId);
        return ResponseEntity.ok(response);
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
    // 좋아요/비추천 수 조회
    @GetMapping("/posts/{id}/reactions")
    public ResponseEntity<PostReactionCountResponse> getReactionCounts(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.getReactionCounts(id));
    }
    //북마크 토클(등록/해제기능)
    @PostMapping("/posts/{id}/bookmark")
    public ResponseEntity<Map<String, String>> toggleBookmark(
            @PathVariable("id") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        boolean added = bookmarkService.toggleBookmark(userId, postId);

        Map<String, String> result = Map.of(
                "message", added ? "북마크 등록됨" : "북마크 해제됨"
        );
        return ResponseEntity.ok(result);
    }
    //내가 북마크한거 조회
    @GetMapping("/bookmarks/me")
    public ResponseEntity<List<PostResponseDto>> getMyBookmarks(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        List<PostResponseDto> bookmarks = bookmarkService.getBookmarksByUser(userId);
        return ResponseEntity.ok(bookmarks);
    }
    //내가 쓴 글 조회
    @GetMapping("/myposts")
    public ResponseEntity<List<PostResponseDto>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        List<PostResponseDto> posts = communityService.getMyPosts(userId);
        return ResponseEntity.ok(posts);
    }
    //내가 댓글 단 게시글 목록 조회
    @GetMapping("/mycomments")
    public ResponseEntity<List<PostResponseDto>> getMyCommentedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        List<PostResponseDto> posts = communityService.getMyCommentedPosts(userId);
        return ResponseEntity.ok(posts);
    }
    //내가 작성한 댓글 목록 조회
    @GetMapping("/my-comments")
    public ResponseEntity<List<CommentResponseDto>> getMyComments(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        List<CommentResponseDto> comments = commentService.getMyComments(userId);
        return ResponseEntity.ok(comments);
    }
    //댓글 단 게시글 목록 조회
    @GetMapping("/my-commented-posts")
    public ResponseEntity<List<PostResponseDto>> getPostsICommented(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        List<PostResponseDto> posts = communityService.getPostsICommented(userId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/my-replied-comments")
    public ResponseEntity<List<CommentResponseDto>> getMyCommentsWithReplies(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        List<CommentResponseDto> comments = commentService.getMyCommentsWithReplies(userId);
        return ResponseEntity.ok(comments);
    }

    // 실시간 인기글 10개 반환
    @GetMapping("/popular")
    public ResponseEntity<List<PostResponseDto>> getBestLivePosts() {
        List<PostResponseDto> bestLivePosts = communityService.getBestLivePosts();
        return ResponseEntity.ok(bestLivePosts);
    }






}
