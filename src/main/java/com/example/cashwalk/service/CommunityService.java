package com.example.cashwalk.service;

import com.example.cashwalk.dto.*;
import com.example.cashwalk.entity.*;
import com.example.cashwalk.exception.PostNotFoundException;
import com.example.cashwalk.repository.*;
import com.example.cashwalk.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final ViewCountService viewCountService; // ✅ 리팩토링된 서비스 주입
    private final UserBlockRepository userBlockRepository;
    private final CommentService commentService;


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
                .views(0)
                .build();

        postRepository.save(post);

        String nickname = userRepository.findById(post.getUserId())
                .map(User::getNickname)
                .orElse("알 수 없음");

        return PostResponseDto.from(post, nickname, 0, 0);
    }

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

    // 게시글 상세 조회 + 댓글 포함 + 조회수 증가
    public PostDetailResponseDto getPostDetail(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        viewCountService.increaseIfNotDuplicate(currentUserId, postId);

        // ✅ 차단 유저 댓글 제외된 목록 불러오기
        List<CommentResponseDto> commentDtos = commentService.getCommentsByPostId(postId, currentUserId);

        int likeCount = postLikeRepository.countByPostIdAndStatus(postId, PostLike.Status.LIKE);
        int commentCount = commentDtos.size();

        boolean likedByMe = postLikeRepository.findByUserIdAndPostId(currentUserId, postId)
                .map(PostLike::getStatus)
                .map(status -> status == PostLike.Status.LIKE)
                .orElse(false);

        return PostDetailResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                .createdAt(post.getCreatedAt())
                .likeCount(likeCount)
                .commentCount(commentCount)
                .views(post.getViews() + 1)
                .likedByMe(likedByMe)
                .comments(commentDtos)
                .build();
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
                // 👍 → 취소
                postLikeRepository.delete(like);
                post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            } else {
                // 👎 → 👍 전환
                like.setStatus(PostLike.Status.LIKE);
                post.setLikeCount(post.getLikeCount() + 1);
            }
        } else {
            // 처음 누르는 경우
            postLikeRepository.save(PostLike.builder()
                    .user(user)
                    .post(post)
                    .status(PostLike.Status.LIKE)
                    .build());
            post.setLikeCount(post.getLikeCount() + 1);
        }

        postRepository.save(post);
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
                // 👎 → 취소
                postLikeRepository.delete(like);
            } else {
                // 👍 → 👎 전환
                like.setStatus(PostLike.Status.DISLIKE);
                post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            }
        } else {
            // 처음 누르는 경우
            postLikeRepository.save(PostLike.builder()
                    .user(user)
                    .post(post)
                    .status(PostLike.Status.DISLIKE)
                    .build());
        }

        postRepository.save(post);
    }


    // 좋아요/싫어요 수 조회
    public PostReactionCountResponse getReactionCounts(Long postId) {
        int likeCount = postLikeRepository.countByPostIdAndStatus(postId, PostLike.Status.LIKE);
        int dislikeCount = postLikeRepository.countByPostIdAndStatus(postId, PostLike.Status.DISLIKE);
        return new PostReactionCountResponse(likeCount, dislikeCount);
    }

    // 게시글 검색 (QueryDSL)
    /* currentUserId가 필요한 이유:
    우리가 "나(currentUserId)가 차단한 유저"들의 글을 보이지 않게 해야 되니까
    👉 당연히 "지금 로그인한 사용자"가 누군지 알아야 함!*/
    public Page<PostResponseDto> searchPosts(PostSearchCondition condition,Long currentUserId) {
        int page = condition.getPage();
        int size = condition.getSize();
        PageRequest pageRequest = PageRequest.of(page, size);

        // ✅ 차단한 유저 ID 리스트 가져오기
        List<Long> blockedUserIds = userBlockRepository.findBlockedUserIdsByBlockerId(currentUserId);

        // ✅ PostRepository의 QueryDSL 쿼리에 blockedUserIds 넘기기
        return postRepository.searchPostsExcludingBlockedUsers(condition, pageRequest, blockedUserIds)
                .map(post -> PostResponseDto.from(
                        post,
                        post.getUser().getNickname(),
                        post.getLikeCount(),
                        post.getCommentCount()
                ));
    }

    // 실시간 인기글/명예의전당 스케줄링
    @Scheduled(fixedRate = 360000)
    @Transactional
    public void updateBoardTypesForPopularPosts() {
        log.info("[스케줄러] 게시글 boardType 자동 업데이트 시작");

        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<Post> recentPosts = postRepository.findPostsCreatedAfter(since);

        for (Post post : recentPosts) {
            int likeCount = postLikeRepository.countByPostIdAndStatus(post.getId(), PostLike.Status.LIKE);

            if (likeCount >= 100 && post.getBoardType() != BoardType.LEGEND) {
                post.setBoardType(BoardType.LEGEND);
                log.info("명예의 전당으로 지정됨 → postId: {}, likes: {}", post.getId(), likeCount);
            } else if (likeCount >= 15 && post.getBoardType() == BoardType.GENERAL) {
                post.setBoardType(BoardType.BESTLIVE);
                log.info("실시간 베스트로 지정됨 → postId: {}, likes: {}", post.getId(), likeCount);
            }
        }

        log.info("[스케줄러] 게시글 boardType 자동 업데이트 완료");
    }

    //내가 작성한 게시글 목록 반환
    @Transactional(readOnly = true)
    public List<PostResponseDto> getMyPosts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Post> myPosts = postRepository.findAllByUser(user);

        return myPosts.stream()
                .map(post -> PostResponseDto.from(
                        post,
                        post.getUser().getNickname(),
                        post.getLikeCount(),
                        post.getCommentCount()
                )).toList();
    }

    //내가 댓글 단 게시글들만 반환 (중복 제거 포함)
    @Transactional(readOnly = true)
    public List<PostResponseDto> getMyCommentedPosts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 내가 쓴 댓글 목록
        List<Comment> myComments = commentRepository.findAllByUser(user);

        // 중복 제거된 게시글만 추출
        return myComments.stream()
                .map(Comment::getPost)
                .distinct()
                .map(post -> PostResponseDto.from(
                        post,
                        post.getUser().getNickname(),
                        post.getLikeCount(),
                        post.getCommentCount()
                )).toList();
    }
    // 내가 댓글 단 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostsICommented(Long userId) {
        List<Post> posts = commentRepository.findDistinctPostsCommentedByUser(userId);

        return posts.stream()
                .map(post -> PostResponseDto.from(
                        post,
                        post.getUser().getNickname(),
                        post.getLikeCount(),
                        post.getCommentCount()
                )).toList();
    }


}
