package com.example.cashwalk.controller;

import com.example.cashwalk.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.cashwalk.dto.CommentRequestDto;
import com.example.cashwalk.dto.CommentResponseDto;
import com.example.cashwalk.dto.CommentUpdateRequestDto;
import com.example.cashwalk.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/community/comments")  // ✅ 수정됨
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ✅ 댓글 작성 (postId는 경로에 포함)
    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId(); // JWT에서 사용자 ID 추출
        CommentResponseDto response = commentService.createComment(postId, userId, dto.getContent());
        return ResponseEntity.ok(response);
    }

    // ✅ 댓글 목록 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @PathVariable Long postId
    ){
        List<CommentResponseDto> comments = commentService.getCommentsByPost(postId);
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


}
