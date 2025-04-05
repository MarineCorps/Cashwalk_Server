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
import com.example.cashwalk.entity.Post;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.exception.CommentNotFoundException;
import com.example.cashwalk.exception.PostNotFoundException;
import com.example.cashwalk.repository.CommentRepository;
import com.example.cashwalk.repository.PostRepository;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.cashwalk.exception.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //댓글 작성
    public CommentResponseDto createComment(Long postId, Long userId, String content){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자가 존재하지 않습니다."));

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(content)
                .build();

        Comment saved = commentRepository.save(comment);

        return CommentResponseDto.builder()
                .id(saved.getId())
                .userId(saved.getUser().getId())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    //댓글 목록 조회
    public List<CommentResponseDto> getCommentsByPost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new PostNotFoundException("게시글이 존재하지 않습니다."));

        List<Comment> comments=commentRepository.findByPostOrderByCreatedAtDesc(post);

        return comments.stream()
                .map(c ->CommentResponseDto.builder()
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
    //댓글 삭제
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("댓글 삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }



//.stream().map().collect()	댓글 리스트를 DTO 리스트로 변환 (Java Stream API)
}
