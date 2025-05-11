package com.example.cashwalk.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostDetailResponseDto {
    private Long id;                  // 게시글 ID
    private String title;            // 제목
    private String nickname;         // 작성자 닉네임
    private String content;          // 본문
    private String imageUrl;         // 이미지 URL
    private Long userId;             // 작성자 ID
    private LocalDateTime createdAt; // 작성 시간

    private int likeCount;           // 좋아요 수
    private int dislikeCount;       // 싫어요 수 (✅ 새로 추가)
    private int commentCount;       // 댓글 수
    private int views;              // 조회 수

    private boolean likedByMe;      // 내가 좋아요 눌렀는지
    private boolean dislikedByMe;   // 내가 싫어요 눌렀는지 ✅ 추가
    private boolean bookmarked;     // 내가 북마크 했는지 (✅ 새로 추가)

    private List<CommentResponseDto> comments;  // 댓글 + 대댓글 목록
}
