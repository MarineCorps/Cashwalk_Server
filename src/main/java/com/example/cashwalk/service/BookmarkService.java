package com.example.cashwalk.service;

import com.example.cashwalk.entity.Bookmark;
import com.example.cashwalk.entity.Post;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.exception.PostNotFoundException;
import com.example.cashwalk.repository.BookmarkRepository;
import com.example.cashwalk.repository.PostRepository;
import com.example.cashwalk.repository.UserRepository;
import com.example.cashwalk.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 북마크 등록/해제 토글
     */
    @Transactional
    public boolean toggleBookmark(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        return bookmarkRepository.findByUserAndPost(user, post)
                .map(bookmark -> {
                    bookmarkRepository.delete(bookmark);
                    return false; // 북마크 해제됨
                })
                .orElseGet(() -> {
                    bookmarkRepository.save(Bookmark.builder()
                            .user(user)
                            .post(post)
                            .build());
                    return true; // 북마크 등록됨
                });
    }

    /**
     * 내가 북마크한 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public List<PostResponseDto> getBookmarksByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Bookmark> bookmarks = bookmarkRepository.findAllByUser(user);

        return bookmarks.stream()
                .map(bookmark -> {
                    Post post = bookmark.getPost();
                    return PostResponseDto.from(
                            post,
                            post.getUser().getNickname(),
                            post.getLikeCount(),
                            post.getCommentCount()
                    );
                }).toList();
    }
}
