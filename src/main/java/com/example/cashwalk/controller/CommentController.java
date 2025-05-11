package com.example.cashwalk.controller;

import com.example.cashwalk.dto.PostResponseDto;
import com.example.cashwalk.entity.BoardType;
import com.example.cashwalk.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.cashwalk.dto.CommentRequestDto;
import com.example.cashwalk.dto.CommentResponseDto;
import com.example.cashwalk.dto.CommentUpdateRequestDto;
import com.example.cashwalk.service.CommentService;
import com.example.cashwalk.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/community/comments")  // ✅ 수정됨
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommunityService communityService;

    // ✅ 댓글 작성 (postId는 경로에 포함)
    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId(); // JWT에서 사용자 ID 추출
        Long parentId = dto.getParentId();     // ✅ 대댓글인 경우 parentId 받기
        CommentResponseDto response = commentService.createComment(postId, userId, dto.getContent(), parentId);
        return ResponseEntity.ok(response);
    }

    // ✅ 댓글 목록 조회 (차단 유저 댓글 제외)
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId(); // ✅ JWT에서 사용자 ID 추출
        List<CommentResponseDto> comments = commentService.getCommentsByPostId(postId, userId);
        return ResponseEntity.ok(comments);
    }


    // ✅ 댓글 수정
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long id,
            @RequestBody CommentUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();  // JWT에서 추출
        CommentResponseDto updated = commentService.updateComment(id, userId, dto.getContent());
        return ResponseEntity.ok(updated);
    }


    // 🔜 댓글 삭제도 여기에서 함께 처리 가능
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        commentService.deleteComment(id, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "댓글이 삭제되었습니다.");

        return ResponseEntity.ok(response);  // 200 OK + 메시지
    }
    // 댓글 추천 요청을 처리하는 엔드포인트 (ex: /comment/42/like)
    @PostMapping("/comment/{id}/like")
    public ResponseEntity<Map<String, String>> likeComment(
            @PathVariable Long id,  // URL 경로에서 댓글 ID 추출 (예: 42)
            @AuthenticationPrincipal CustomUserDetails userDetails  // JWT에서 사용자 정보 추출
    ){
        System.out.println("✅ [Controller] likeComment 호출됨 - commentId: " + id + ", userId: " + userDetails.getUserId());
        // 실제 추천 처리 로직 호출 (서비스 계층에 위임)
        commentService.likeComment(id, userDetails.getUserId());

        // 프론트에 보낼 응답 메시지를 준비 (Map 형태)
        Map<String, String> response = new HashMap<>();
        response.put("message", "좋아요");

        // HTTP 200 OK + JSON 형태로 응답 전송
        return ResponseEntity.ok(response);
    }

    // 댓글 비추천 요청을 처리하는 엔드포인트 (ex: /comment/42/dislike)
    @PostMapping("/comment/{id}/dislike")
    public ResponseEntity<Map<String,String>> dislikeComment(
            @PathVariable Long id,  // URL 경로에서 댓글 ID 추출
            @AuthenticationPrincipal CustomUserDetails userDetails  // JWT에서 사용자 정보 추출
    ){
        System.out.println("✅ [Controller] dislikeComment 호출됨 - commentId: " + id + ", userId: " + userDetails.getUserId());
        // 실제 비추천 처리 로직 호출
        commentService.dislikeComment(id, userDetails.getUserId());

        // 응답 메시지 구성
        Map<String,String> response = new HashMap<>();
        response.put("message","비추천");

        // HTTP 200 OK 응답
        return ResponseEntity.ok(response);
    }

    //추천/비추천 개수 조회
    @GetMapping("/{id}/reactions")
    public ResponseEntity<Map<String, Integer>> getCommentReactions(@PathVariable Long id) {
        Map<String, Integer> reactionCounts = commentService.getReactionCounts(id);
        return ResponseEntity.ok(reactionCounts);
    }

}
