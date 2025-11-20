package com.multi.backend5_1_multi_fc.community.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


public class CommunityDto {

    // === 게시글 ===
    // 게시글 작성
    @Data
    public static class PostCreateRequest{
    private String category;  //일반 유저는 자유게시판만
    private String title;
    private String content;
    private String imageUrl;
    }

    // 게시글 수정
    @Data
    public static class PostUpdateRequest {
        private String title;
        private String content;
    }

    // 게시글 목록
    @Data
    public static class PostListResponse {
        private Long postId;
        private String category;
        private String title;
        private Long commentCount;
        private String writerNickname;
        private LocalDateTime createdAt;
        private Long viewCount;
    }

    // 게시글 상세
    @Data
    public static class PostDetailResponse {
        private Long postId;
        private String category;
        private String title;
        private String content;
        private String writerNickname;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long viewCount;
        private Long commentCount;
        private String imageUrl;
        private List<CommentResponse> comments; //댓글 리스트
    }

    // === 댓글, 대댓글 ===
    // 댓글 작성
    @Data
    public static class CommentCreateRequest {
        private Long parentCommentId;   // null이면 일반 댓글, 값 있으면 대댓글
        private String content;
    }

    // 댓글 수정
    @Data
    public static class CommentUpdateRequest {
        private String content;
    }

    // 댓글 조회용
    @Data
    public static class CommentResponse {
        private Long commentId;
        private Long postId;
        private Long parentCommentId; // 부모 댓글 ID (대댓글용)
        private String commentWriterNickname;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

}
