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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final BookmarkRepository bookmarkRepository;

    // 게시글 작성
    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto, MultipartFile imageFile) {
        log.debug("🟡 [게시글 생성] userId in requestDto: {}", requestDto.getUserId());

        // 🔒 1. 유효한 사용자 ID인지 확인
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        // 🖼️ 2. 이미지 업로드 처리 (없으면 null)
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = FileUploadUtil.saveFile(imageFile);
        }

        // 📝 3. 게시글 객체 생성
        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .user(user) // ✅ 핵심: userId가 아니라 user 객체 직접 넣기
                .boardType(BoardType.valueOf(requestDto.getBoardType()))
                .postCategory(PostCategory.valueOf(requestDto.getPostCategory())) // 유동적으로 처리
                .imageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .views(0)
                .bookmarkCount(0)
                .build();

        // 💾 4. 저장
        postRepository.save(post);

        // 🧾 5. 응답 DTO 생성 (nickname 포함)
        String nickname = user.getNickname();
        log.debug("🟢 [게시글 저장 완료] postId: {}, userId: {}, nickname: {}", post.getId(), user.getId(), nickname);

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

    @Transactional
    public PostDetailResponseDto getPostDetail(Long postId, Long currentUserId) {
        // ✅ 1. 게시글 조회 (작성자 포함)
        Post post = postRepository.findWithUserById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        // ✅ 2. Redis로 중복 방지 후 조회수 증가
        viewCountService.increaseIfNotDuplicate(currentUserId, postId);

        // ✅ 3. 댓글 목록 (차단 유저 제외, 대댓글 포함)
        List<CommentResponseDto> commentDtos = commentService.getCommentsByPostId(postId, currentUserId);

        // ✅ 4. 추천 / 비추천 수
        int likeCount = postLikeRepository.countByPostIdAndStatus(postId, PostLike.Status.LIKE);
        int dislikeCount = postLikeRepository.countByPostIdAndStatus(postId, PostLike.Status.DISLIKE);

        // ✅ 5. 댓글 수
        int commentCount = commentDtos.size();

        // ✅ 6. 내가 추천 / 비추천 눌렀는지
        Optional<PostLike> likeOptional = postLikeRepository.findByUserIdAndPostId(currentUserId, postId);
        boolean likedByMe = likeOptional.map(l -> l.getStatus() == PostLike.Status.LIKE).orElse(false);
        boolean dislikedByMe = likeOptional.map(l -> l.getStatus() == PostLike.Status.DISLIKE).orElse(false);

        // ✅ 7. 북마크 여부
        boolean bookmarked = bookmarkRepository.existsByUserIdAndPostId(currentUserId, postId);

        // ✅ 8. DTO 빌드 및 리턴
        return PostDetailResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                .createdAt(post.getCreatedAt())
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .commentCount(commentCount)
                .views(post.getViews())
                .likedByMe(likedByMe)
                .dislikedByMe(dislikedByMe) // ✅ 추가됨!
                .bookmarked(bookmarked)
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

    // ✅ 좋아요 토글 - 엔티티 필드 수정 제거됨
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
            } else {
                // 👎 → 👍 전환
                like.setStatus(PostLike.Status.LIKE);
                postLikeRepository.save(like);
            }
        } else {
            // 처음 누르는 경우
            postLikeRepository.save(PostLike.builder()
                    .user(user)
                    .post(post)
                    .status(PostLike.Status.LIKE)
                    .build());
        }
    }
    // ✅ 싫어요 토글 - 엔티티 필드 수정 제거됨
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
                postLikeRepository.save(like);
            }
        } else {
            // 처음 누르는 경우
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
    // 게시글 검색 (QueryDSL)
    /* currentUserId가 필요한 이유:
    우리가 "나(currentUserId)가 차단한 유저"들의 글을 보이지 않게 해야 되니까
    👉 당연히 "지금 로그인한 사용자"가 누군지 알아야 함!*/
    public Page<PostResponseDto> searchPosts(PostSearchCondition condition, Long currentUserId) {
        int page = condition.getPage();
        int size = condition.getSize();
        PageRequest pageRequest = PageRequest.of(page, size);

        // ✅ 1. 차단한 유저 ID 리스트
        List<Long> blockedUserIds = userBlockRepository.findBlockedUserIdsByBlockerId(currentUserId);

        // ✅ 2. 게시글 목록 (차단 제외된 사용자 기준)
        Page<Post> posts = postRepository.searchPostsExcludingBlockedUsers(condition, pageRequest, blockedUserIds);

        // ✅ 3. 각 post마다 추천/댓글 수 계산하여 DTO로 변환
        return posts.map(post -> {
            int likeCount = postLikeRepository.countByPostIdAndStatus(post.getId(), PostLike.Status.LIKE);
            int commentCount = commentRepository.countByPostId(post.getId());

            return PostResponseDto.from(
                    post,
                    post.getUser().getNickname(),
                    likeCount,
                    commentCount
            );
        });
    }
    // 실시간 인기글
    @Scheduled(fixedRate = 1000*60*10)
    @Transactional
    public void updateBestLivePosts() {
        log.info("[스케줄러] BESTLIVE 자동 지정 시작");

        // 기존 BESTLIVE 초기화
        List<Post> currentBest = postRepository.findByPostCategory(PostCategory.BESTLIVE);
        for (Post p : currentBest) {
            p.setPostCategory(PostCategory.GENERAL);
            postRepository.save(p);
        }

        // 상위 10개 점수순 게시글 → BESTLIVE 지정
        List<Post> topPosts = postRepository.findTop10BestLivePostsByScore();
        for (Post p : topPosts) {
            p.setPostCategory(PostCategory.BESTLIVE);
            postRepository.save(p);
            log.info("🔥 BESTLIVE 지정 → postId: {}, title: {}", p.getId(), p.getTitle());
        }

        log.info("[스케줄러] BESTLIVE 자동 지정 완료");
    }
    //명예의 전당
    @Scheduled(fixedRate = 1000*60*1440)
    @Transactional
    public void updateLegendPosts() {
        log.info("[스케줄러] LEGEND 자동 지정 시작");

        double threshold = 8000.0; // 북마크 반영한 기준점

        List<Post> candidates = postRepository.findLegendCandidatesByScore(threshold);

        for (Post post : candidates) {
            if (post.getPostCategory() != PostCategory.LEGEND) {
                post.setPostCategory(PostCategory.LEGEND);
                postRepository.save(post);
                log.info("👑 LEGEND 지정 → postId: {}, score 예상: {}", post.getId(), "(북마크: " + post.getBookmarkCount() + ")");
            }
        }

        log.info("[스케줄러] LEGEND 자동 지정 완료");
    }
    // 내가 작성한 게시글 목록 반환
    @Transactional(readOnly = true)
    public List<PostResponseDto> getMyPosts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Post> myPosts = postRepository.findAllByUser(user);

        return myPosts.stream()
                .map(post -> {
                    int likeCount = postLikeRepository.countByPostIdAndStatus(post.getId(), PostLike.Status.LIKE);
                    int commentCount = commentRepository.countByPostId(post.getId());
                    return PostResponseDto.from(post, post.getUser().getNickname(), likeCount, commentCount);
                })
                .toList();
    }
    // 내가 댓글 단 게시글들만 반환 (중복 제거 포함)
    @Transactional(readOnly = true)
    public List<PostResponseDto> getMyCommentedPosts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Comment> myComments = commentRepository.findAllByUser(user);

        return myComments.stream()
                .map(Comment::getPost)
                .distinct()
                .map(post -> {
                    int likeCount = postLikeRepository.countByPostIdAndStatus(post.getId(), PostLike.Status.LIKE);
                    int commentCount = commentRepository.countByPostId(post.getId());
                    return PostResponseDto.from(post, post.getUser().getNickname(), likeCount, commentCount);
                })
                .toList();
    }
    // 내가 댓글 단 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostsICommented(Long userId) {
        List<Post> posts = commentRepository.findDistinctPostsCommentedByUser(userId);

        return posts.stream()
                .map(post -> {
                    int likeCount = postLikeRepository.countByPostIdAndStatus(post.getId(), PostLike.Status.LIKE);
                    int commentCount = commentRepository.countByPostId(post.getId());
                    return PostResponseDto.from(post, post.getUser().getNickname(), likeCount, commentCount);
                })
                .toList();
    }
    // 실베글 10개 리턴
    @Transactional(readOnly = true)
    public List<PostResponseDto> getBestLivePosts() {
        List<Post> posts = postRepository.findTop10BestLivePostsByScore();

        return posts.stream()
                .map(post -> {
                    int likeCount = postLikeRepository.countByPostIdAndStatus(post.getId(), PostLike.Status.LIKE);
                    int commentCount = commentRepository.countByPostId(post.getId());
                    return PostResponseDto.from(post, post.getUser().getNickname(), likeCount, commentCount);
                })
                .toList();
    }
}
