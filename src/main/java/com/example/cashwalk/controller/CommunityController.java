package com.example.cashwalk.controller;

import com.example.cashwalk.dto.PostDetailResponseDto;
import com.example.cashwalk.dto.PostRequestDto;
import com.example.cashwalk.dto.PostResponseDto;
import com.example.cashwalk.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;

    //게시글 작성
    @PostMapping("/posts")
    public ResponseEntity<PostResponseDto> createPost(
            @RequestParam("content") String content,
            @RequestParam("userId") Long userId,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        PostRequestDto requestDto = new PostRequestDto();
        requestDto.setContent(content);
        requestDto.setUserId(userId);

        PostResponseDto response = communityService.createPost(requestDto, imageFile);
        return ResponseEntity.ok(response);
    }


    //게시글 목록 조회(페이징 지원)
    @GetMapping("/posts")
    public ResponseEntity<Page<PostResponseDto>> getPostList(Pageable pageable){
        Page<PostResponseDto> posts=communityService.getPostList(pageable);
        return ResponseEntity.ok(posts);
    }
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long id) {
        PostResponseDto post = communityService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/posts/{id}") //put요청으로 URL경로의 id추출
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long id,
            @RequestParam("content") String content,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        PostResponseDto updated = communityService.updatePost(id, content, imageFile);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        communityService.deletePost(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }

    //게시글 ID에 해당하는 상세 + 댓글 포함 정보를 조회하는 요청 처리
    @GetMapping("/posts/{id}/detail")
    public ResponseEntity<PostDetailResponseDto> getPostDetail(@PathVariable Long id) {
        PostDetailResponseDto postDetail=communityService.getPostDetail(id);
        return ResponseEntity.ok(postDetail);
    }



}

//@RequsetPart -> Multipart 요청에서
// 각각의 파트를 분리해서 받음 (JSON + 이미지 동시에 처리)
