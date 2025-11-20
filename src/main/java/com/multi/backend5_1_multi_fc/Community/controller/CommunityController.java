package com.multi.backend5_1_multi_fc.community.controller;

import com.multi.backend5_1_multi_fc.community.dto.CommunityDto;
import com.multi.backend5_1_multi_fc.community.exception.CommunityException;
import com.multi.backend5_1_multi_fc.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    // === 현재 로그인 정보 가져오기 ===
    private Authentication getAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new CommunityException("로그인이 필요한 요청입니다.");
        }
        return auth;
    }

    private String getCurrentUsername() {
        return getAuth().getName(); // username
    }

    private String getCurrentRole() {
        return getAuth().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
    }

    // ===== 게시글 =====

    // 목록 (비로그인 가능)
    @GetMapping("/posts")
    public ResponseEntity<List<CommunityDto.PostListResponse>> getPosts(
            @RequestParam("category") String category
    ) {
        return ResponseEntity.ok(communityService.getPostsByCategory(category));
    }

    // 상세 (비로그인 가능)
    @GetMapping("/posts/{postId}")
    public ResponseEntity<CommunityDto.PostDetailResponse> getPostDetail(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(communityService.getPostDetail(postId));
    }

    // 작성 (로그인 필요)
    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createPost(
            @RequestPart("post") CommunityDto.PostCreateRequest req,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        String username = getCurrentUsername();
        String role = getCurrentRole();           // ROLE_ADMIN / ROLE_USER 등
        communityService.createPost(username, role, req, image);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 수정
    @PutMapping("/posts/{postId}")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long postId,
            @RequestBody CommunityDto.PostUpdateRequest req
    ) {
        String username = getCurrentUsername();
        communityService.updatePost(postId, username, req);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId
    ) {
        String username = getCurrentUsername();
        communityService.deletePost(postId, username);
        return ResponseEntity.noContent().build();
    }

    // ===== 댓글 =====

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Void> createComment(
            @PathVariable Long postId,
            @RequestBody CommunityDto.CommentCreateRequest req
    ) {
        String username = getCurrentUsername();
        communityService.createComment(postId, username, req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommunityDto.CommentUpdateRequest req
    ) {
        String username = getCurrentUsername();
        communityService.updateComment(commentId, username, req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId
    ) {
        String username = getCurrentUsername();
        communityService.deleteComment(commentId, username);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(CommunityException.class)
    public ResponseEntity<String> handleCommunityException(CommunityException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
