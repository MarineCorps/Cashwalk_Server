package com.example.cashwalk.service;

/*📁 📷 이미지 URL 테스트
Spring Boot는 기본적으로 resources/static 안에 있는 파일은
http://localhost:8080/uploads/파일이름 으로 접근 가능*/
import com.example.cashwalk.dto.PostRequestDto;
import com.example.cashwalk.dto.PostResponseDto;
import com.example.cashwalk.entity.Post;
import com.example.cashwalk.repository.CommentRepository;
import com.example.cashwalk.repository.PostRepository;
import com.example.cashwalk.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.cashwalk.exception.PostNotFoundException;
import com.example.cashwalk.dto.CommentResponseDto;
import com.example.cashwalk.dto.PostDetailResponseDto;
import com.example.cashwalk.entity.Comment;
import java.util.*;

@Service
@RequiredArgsConstructor  // final 필드를 자동으로 주입해주는 생성자 생성
public class CommunityService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 게시글 작성 (이미지 + 내용)
    public PostResponseDto createPost(PostRequestDto requestDto, MultipartFile imageFile) {
        String imageUrl = null;

        // 이미지가 있는 경우, 서버에 저장 후 URL 반환
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = FileUploadUtil.saveFile(imageFile);
        }

        Post post = Post.builder()
                .content(requestDto.getContent())
                .userId(requestDto.getUserId())
                .imageUrl(imageUrl)
                .build();

        Post saved = postRepository.save(post);

        return PostResponseDto.builder()
                .id(saved.getId())
                .content(saved.getContent())
                .userId(saved.getUserId())
                .imageUrl(saved.getImageUrl())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // 게시글 목록 조회 (페이징)
    public Page<PostResponseDto> getPostList(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(post -> PostResponseDto.builder()
                        .id(post.getId())
                        .content(post.getContent())
                        .userId(post.getUserId())
                        .imageUrl(post.getImageUrl())
                        .createdAt(post.getCreatedAt())
                        .build()
                );
    }
    //게시글 작성
    public PostResponseDto getPostById(Long id) {
        Post post=postRepository.findById(id)
                .orElseThrow(()->new PostNotFoundException("게시글이 존재하지 않습니다."));

        return PostResponseDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .userId(post.getUserId())
                .createdAt(post.getCreatedAt())
                .build();
    }
    //게시글 수정
    public PostResponseDto updatePost(Long id, String content,MultipartFile imageFile) {
        Post post =postRepository.findById(id)
                .orElseThrow(()-> new PostNotFoundException("게시글이 존재하지 않습니다."));
        post.setContent(content); //게시글 내용 수정

        if(imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = FileUploadUtil.saveFile(imageFile);
            post.setImageUrl(imageUrl); //이미지가 있따면 새로 저장 후 교체
        }
        Post updated = postRepository.save(post); //변경된 내용을 DB에 저장

        return PostResponseDto.builder()
                .id(updated.getId())
                .content(updated.getContent())
                .userId(updated.getUserId())
                .imageUrl(updated.getImageUrl())
                .createdAt(updated.getCreatedAt())
                .build();
    }
    //게시글 삭제
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));
        postRepository.delete(post); //삭제 처리
        //해당 엔티티를 DB에서 삭제(JPA가 SQL DELETE 쿼리 수행)
    }
    //게시글 + 댓글 통합 데이터를 반환하는 서비스 로직
    public PostDetailResponseDto getPostDetail(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        // 댓글 리스트 조회 (Post 기준)
        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtDesc(post);

        // 댓글 → DTO 변환
        List<CommentResponseDto> commentDtoList = comments.stream()
                .map(comment -> CommentResponseDto.builder()
                        .id(comment.getId())
                        .userId(comment.getUser().getId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .toList();

        // 게시글 + 댓글을 묶어서 반환
        return PostDetailResponseDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .userId(post.getUserId())
                .createdAt(post.getCreatedAt())
                .comments(commentDtoList)
                .build();
    }




}
