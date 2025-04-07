package com.example.cashwalk.service;

import com.example.cashwalk.dto.*;
import com.example.cashwalk.entity.*;
import com.example.cashwalk.exception.PostNotFoundException;
import com.example.cashwalk.repository.*;
import com.example.cashwalk.utils.FileUploadUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.cashwalk.service.RedisService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final RedisService redisService;
    // 게시글 작성
    public PostResponseDto createPost(PostRequestDto requestDto, MultipartFile imageFile) {
        String imageUrl = (imageFile != null && !imageFile.isEmpty())
                ? FileUploadUtil.saveFile(imageFile)
                : null;

        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .userId(requestDto.getUserId())
                .boardType(BoardType.valueOf(requestDto.getBoardType()))
                .imageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .views(0) // 조회수 초기값
                .build();

        postRepository.save(post);

        String nickname = userRepository.findById(post.getUserId())
                .map(User::getNickname)
                .orElse("알 수 없음");

        return PostResponseDto.from(post, nickname, 0, 0);
    }

    // 게시글 상세 조회 + 조회수 증가
    // ✅ 수정: 조회수 증가 제거
    public PostResponseDto getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글이 존재하지 않습니다."));

        String nickname = userRepository.findById(post.getUserId())
                .map(User::getNickname)
                .orElse("알 수 없음");

        int likeCount = postLikeRepository.countByPostIdAndStatus(post.getId(), PostLike.Status.LIKE);
        int commentCount = commentRepository.countByPostId(post.getId());

        return PostResponseDto.from(post, nickname, likeCount, commentCount);
    }

    // 조회수 증가 로직 (중복 방지)
    @Transactional
    public void increaseViewCountIfNotDuplicate(Long userId, Long postId) {
        // Redis에 해당 사용자의 조회 기록이 없다면
        if (!redisService.hasViewPost(userId, postId)) {
            System.out.println("Redis에 기록 없음 -> 조회수 증가ㄱㄱ");

            // 조회수 증가
            postRepository.incrementViewCount(postId);
            // Redis에 기록 (TTL: 1시간)
            redisService.markPostAsViewed(userId, postId);
        }
        else{
            System.out.println("이미 조회한 게시글->조회수 증가는 못해요~");
        }
        System.out.println("Redis Key: viewed:" + userId + ":" + postId);


    }




    // 게시글 수정
    public PostResponseDto updatePost(Long postId, String content, MultipartFile imageFile) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글이 존재하지 않습니다."));

        post.setContent(content);
        if (imageFile != null && !imageFile.isEmpty()) {
            post.setImageUrl(FileUploadUtil.saveFile(imageFile));
        }

        postRepository.save(post);

        String nickname = userRepository.findById(post.getUserId())
                .map(User::getNickname)
                .orElse("알 수 없음");

        int likeCount = postLikeRepository.countByPostIdAndStatus(post.getId(), PostLike.Status.LIKE);
        int commentCount = commentRepository.countByPostId(post.getId());

        return PostResponseDto.from(post, nickname, likeCount, commentCount);
    }

    // 게시글 삭제
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글이 존재하지 않습니다."));
        postRepository.delete(post);
    }

    // 게시글 리스트 조회 (정렬 지원)
    public Page<PostResponseDto> getPostList(BoardType boardType, String sort, Pageable pageable) {
        Page<Object[]> results;

        if ("likes".equals(sort)) {
            results = postRepository.findAllOrderByLikes(
                    boardType != null ? boardType.name() : null,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        } else if ("comments".equals(sort)) {
            results = postRepository.findAllOrderByCommentCount(
                    boardType != null ? boardType.name() : null,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        } else if ("views".equals(sort)) {
            results = postRepository.findAllOrderByViews(
                    boardType != null ? boardType.name() : null,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        } else {
            Page<Post> postPage = postRepository.findAllByBoardTypeOrderByCreatedAtDesc(boardType, pageable);
            return postPage.map(post -> {
                String nickname = userRepository.findById(post.getUserId())
                        .map(User::getNickname)
                        .orElse("알 수 없음");
                int likeCount = postLikeRepository.countByPostIdAndStatus(post.getId(), PostLike.Status.LIKE);
                int commentCount = commentRepository.countByPostId(post.getId());
                return PostResponseDto.from(post, nickname, likeCount, commentCount);
            });
        }

        return results.map(PostResponseDto::fromObjectArray);
    }

    // 게시글 상세 + 댓글 포함 응답
    public PostDetailResponseDto getPostDetail(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtDesc(post);
        List<CommentResponseDto> commentDtos = comments.stream()
                .map(comment -> CommentResponseDto.builder()
                        .id(comment.getId())
                        .userId(comment.getUser().getId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .toList();

        return PostDetailResponseDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .userId(post.getUserId())
                .createdAt(post.getCreatedAt())
                .comments(commentDtos)
                .build();
    }

    // 좋아요 토글
    @Transactional
    public void likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));

        Optional<PostLike> optional = postLikeRepository.findByUserAndPost(user, post);

        if (optional.isPresent()) {
            PostLike like = optional.get();
            if (like.getStatus() == PostLike.Status.LIKE) {
                postLikeRepository.delete(like);
            } else {
                like.setStatus(PostLike.Status.LIKE);
            }
        } else {
            postLikeRepository.save(PostLike.builder()
                    .user(user)
                    .post(post)
                    .status(PostLike.Status.LIKE)
                    .build());
        }
    }

    // 싫어요 토글
    @Transactional
    public void dislikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));

        Optional<PostLike> optional = postLikeRepository.findByUserAndPost(user, post);

        if (optional.isPresent()) {
            PostLike like = optional.get();
            if (like.getStatus() == PostLike.Status.DISLIKE) {
                postLikeRepository.delete(like);
            } else {
                like.setStatus(PostLike.Status.DISLIKE);
            }
        } else {
            postLikeRepository.save(PostLike.builder()
                    .user(user)
                    .post(post)
                    .status(PostLike.Status.DISLIKE)
                    .build());
        }
    }

    // 좋아요/싫어요 수 조회
    public PostReactionCountResponse getReactionCounts(Long postId) {
        int likeCount = postLikeRepository.countByPostIdAndStatus(postId, PostLike.Status.LIKE);
        int dislikeCount = postLikeRepository.countByPostIdAndStatus(postId, PostLike.Status.DISLIKE);
        return new PostReactionCountResponse(likeCount, dislikeCount);
    }
}
