package com.example.cashwalk.dto;

import com.example.cashwalk.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponseDto {

    private Long id;               // 댓글 ID
    private Long userId;           // 작성자 ID
    private String nickname;       // 작성자 닉네임
    private String content;        // 댓글 내용
    private LocalDateTime createdAt; // 작성 시각

    private int likeCount;         // 좋아요 수
    private int dislikeCount;      // 비추천 수
    private boolean likedByMe;     // 내가 좋아요 눌렀는지
    private boolean dislikedByMe;  // 내가 비추천 눌렀는지

    private Long parentId;         // 대댓글이면 부모 ID

    @JsonProperty("isMine")
    private boolean isMine;        // ✅ 내가 작성한 댓글 여부 (프론트에서 분기처리용)

    // ✅ 기본 변환 메서드 (실제 좋아요 여부 등은 나중에 set)
    public static CommentResponseDto from(Comment comment, Long currentUserId) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .likeCount(0)  // 나중에 서비스 레이어에서 설정
                .dislikeCount(0)
                .likedByMe(false)
                .dislikedByMe(false)
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .isMine(comment.getUser().getId().equals(currentUserId)) // ✅ 핵심
                .build();
    }

    // ✅ 모든 정보 수동 세팅용 생성자 (예: CommentService에서 직접 주입)
    public static CommentResponseDto of(
            Comment comment,
            Long currentUserId,
            int likeCount,
            int dislikeCount,
            boolean likedByMe,
            boolean dislikedByMe
    ) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .likedByMe(likedByMe)
                .dislikedByMe(dislikedByMe)
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .isMine(comment.getUser().getId().equals(currentUserId))
                .build();
    }
}
