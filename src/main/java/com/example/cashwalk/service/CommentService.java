/*[POST] /api/community/posts/{postId}/comments
→ CommentController
→ CommentService
→ CommentRepository.save()

[GET] /api/community/posts/{postId}/comments
→ CommentController
→ CommentService
→ CommentRepository.findByPostIdOrderByCreatedAtDesc()
*/

package com.example.cashwalk.service;
import com.example.cashwalk.dto.CommentRequestDto;
import com.example.cashwalk.dto.CommentResponseDto;
import com.example.cashwalk.dto.CommentUpdateRequestDto;
import com.example.cashwalk.entity.Comment;
import com.example.cashwalk.entity.CommentReaction;
import com.example.cashwalk.entity.Post;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.exception.CommentNotFoundException;
import com.example.cashwalk.exception.PostNotFoundException;
import com.example.cashwalk.repository.*;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.cashwalk.exception.AccessDeniedException;

import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentReactionRepository commentReactionRepository;
    private final UserBlockRepository userBlockRepository;

    // 댓글 작성
    @Transactional
    public CommentResponseDto createComment(Long postId, Long userId, String content, Long parentId) {
        // 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        // 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자가 존재하지 않습니다."));

        // 대댓글인 경우 → 부모 댓글 조회
        Comment parentComment = null;
        if (parentId != null) {
            parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
        }

        // 댓글 생성 (부모가 null이면 일반 댓글, 아니면 대댓글)
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(content)
                .parent(parentComment)  // ✅ 여기 주목
                .build();

        Comment saved = commentRepository.save(comment);

        // 댓글 수 증가
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        // 좋아요/비추천 수 계산
        long likeCount = commentReactionRepository.countByCommentAndStatus(saved, CommentReaction.Status.LIKE);
        long dislikeCount = commentReactionRepository.countByCommentAndStatus(saved, CommentReaction.Status.DISLIKE);

        // DTO 반환
        return CommentResponseDto.builder()
                .id(saved.getId())
                .userId(user.getId())
                .nickname(user.getNickname())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .likeCount((int) likeCount)
                .dislikeCount((int) dislikeCount)
                .likedByMe(false)
                .dislikedByMe(false)
                .parentId(parentId)  // ✅ parentId 포함
                .build();
    }




    public List<CommentResponseDto> getCommentsByPost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtDesc(post);

        return comments.stream()
                .map(c -> CommentResponseDto.builder()
                        .id(c.getId())
                        .userId(c.getUser().getId())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .build()
                ).collect(Collectors.toList());
    }

    //댓글 수정
    public CommentResponseDto updateComment(Long commentId, Long userId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("댓글 수정 권한이 없습니다.");
        }

        comment.setContent(content);
        Comment updated = commentRepository.save(comment);

        return CommentResponseDto.builder()
                .id(updated.getId())
                .userId(updated.getUser().getId())
                .content(updated.getContent())
                .createdAt(updated.getCreatedAt())
                .build();
    }
    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("댓글 삭제 권한이 없습니다.");
        }

        Post post = comment.getPost();
        commentRepository.delete(comment);

        // ✅ 댓글 수 감소
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
        postRepository.save(post);
    }


    //추천
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        System.out.println("✅ [Service] likeComment 호출됨 - commentId: " + commentId + ", userId: " + userId);
        Comment comment =commentRepository.findById(commentId)
                .orElseThrow(()->new CommentNotFoundException("댓글이 존재하지 않습니다."));

        User user=userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("사용자가 존재하지않습니다."));

        Optional<CommentReaction> existing=commentReactionRepository.findByUserAndComment(user,comment);

        if(existing.isPresent()){
            CommentReaction reaction=existing.get();
            System.out.println("➡️ 기존 반응 존재: " + reaction.getStatus());
            if(reaction.getStatus()==CommentReaction.Status.LIKE){
                System.out.println("🗑️ 기존 좋아요 삭제");
                commentReactionRepository.delete(reaction); // 👍 → 취소
            }
            else{
                System.out.println("🔄 비추천 → 좋아요 전환");
                reaction.setStatus(CommentReaction.Status.LIKE); // 👎 → 👍 전환
            }
        }
        else{
            System.out.println("➕ 좋아요 새로 생성");
            CommentReaction newReaction=CommentReaction.builder()
                    .user(user)
                    .comment(comment)
                    .status(CommentReaction.Status.LIKE)
                    .build();
            commentReactionRepository.save(newReaction);
        }
    }
    //비추천
    @Transactional
    public void dislikeComment(Long commentId, Long userId) {
        Comment comment =commentRepository.findById(commentId)
                .orElseThrow(()->new CommentNotFoundException("댓글이 존재하지 않습니다."));

        User user=userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("사용자가 존재하지않습니다."));

        Optional<CommentReaction> existing=commentReactionRepository.findByUserAndComment(user,comment);

        if(existing.isPresent()){
            CommentReaction reaction=existing.get();
            if(reaction.getStatus()==CommentReaction.Status.DISLIKE){
                commentReactionRepository.delete(reaction); // 👍 → 취소
            }
            else{
                reaction.setStatus(CommentReaction.Status.DISLIKE); // 👎 → 👍 전환
            }
        }
        else{
            CommentReaction newReaction=CommentReaction.builder()
                    .user(user)
                    .comment(comment)
                    .status(CommentReaction.Status.DISLIKE)
                    .build();
            commentReactionRepository.save(newReaction);
        }
    }
    //추천개수 조회용 메서드
    public Map<String, Integer> getReactionCounts(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글이 존재하지 않습니다."));

        int likeCount = commentReactionRepository.countByCommentAndStatus(comment, CommentReaction.Status.LIKE);
        int dislikeCount = commentReactionRepository.countByCommentAndStatus(comment, CommentReaction.Status.DISLIKE);

        Map<String, Integer> result = new HashMap<>();
        result.put("likeCount", likeCount);
        result.put("dislikeCount", dislikeCount);
        return result;
    }
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getMyComments(Long userId) {
        // 🔍 사용자 유효성 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 📥 내가 작성한 댓글 전체 조회 (최신순)
        List<Comment> comments = commentRepository.findAllByUserOrderByCreatedAtDesc(user);

        // 🔁 DTO 변환 (내가 작성한 댓글이므로 isMine은 무조건 true)
        return comments.stream()
                .map(comment -> CommentResponseDto.from(comment, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getMyCommentsWithReplies(Long userId) {
        // 📥 내가 작성한 댓글 + 해당 댓글에 달린 대댓글 조회
        List<Comment> comments = commentRepository.findMyCommentsWithReplies(userId);

        // 🔁 DTO 변환 (내가 작성한 댓글이므로 isMine은 true)
        return comments.stream()
                .map(comment -> CommentResponseDto.from(comment, userId))
                .collect(Collectors.toList());
    }

    /**
     * ✅ 특정 게시글의 댓글 목록 조회
     * - 차단한 유저의 댓글/대댓글은 필터링함
     * - 로그인한 사용자의 좋아요/싫어요/작성 여부를 반영함
     */
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByPostId(Long postId, Long currentUserId) {
        // 🔍 로그인한 사용자가 차단한 유저 ID 목록 조회
        List<Long> blockedUserIds = userBlockRepository.findBlockedUserIdsByBlockerId(currentUserId);

        // 🔍 게시글 존재 여부 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 📥 해당 게시글의 모든 댓글 조회 (최신순)
        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtDesc(post);

        return comments.stream()
                // 🔒 차단한 유저가 작성한 댓글은 제외
                .filter(c -> !blockedUserIds.contains(c.getUser().getId()))
                // 🔒 대댓글일 경우, 부모 댓글 작성자도 차단 유저면 제외
                .filter(c -> c.getParent() == null || !blockedUserIds.contains(c.getParent().getUser().getId()))
                // 🔁 DTO 변환 (좋아요, isMine, parentId 등 포함)
                .map(c -> CommentResponseDto.from(c, currentUserId))
                .collect(Collectors.toList());
    }


//.stream().map().collect()	댓글 리스트를 DTO 리스트로 변환 (Java Stream API)
}
