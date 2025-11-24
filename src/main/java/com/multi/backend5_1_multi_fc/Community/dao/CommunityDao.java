package com.multi.backend5_1_multi_fc.community.dao;

import com.multi.backend5_1_multi_fc.community.dto.CommunityDto;
import com.multi.backend5_1_multi_fc.community.mapper.CommentMapper;
import com.multi.backend5_1_multi_fc.community.mapper.CommunityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommunityDao {

    private final CommunityMapper communityMapper;
    private final CommentMapper commentMapper;

    public Long findUserIdByUsername(String username) {
        return communityMapper.findUserIdByUsername(username);
    }

    // === 게시글 ===

    // 게시글 작성
    public void insertPost(Long userId, CommunityDto.PostCreateRequest req, String imageUrl) {
        communityMapper.insertPost(userId, req, imageUrl);
    }

    // 게시글 목록
    public List<CommunityDto.PostListResponse> findPostByCategory(String category) {
        return communityMapper.findPostByCategory(category);
    }

    // 게시글 상세
    public CommunityDto.PostDetailResponse findPostDetail(Long postId) {
        return communityMapper.findPostDetail(postId);
    }

    // 게시글 수정
    public int updatePostByWriter(Long postId, Long userId, CommunityDto.PostUpdateRequest req) {
        return communityMapper.updatePostByWriter(postId, userId, req);
    }

    // 게시글 삭제
    public int deletePostByWriter(Long postId, Long userId) {
        return communityMapper.deletePostByWriter(postId, userId);
    }

    // 게시글 조회수 증가
    public void increaseViewCount(Long postId) {
        communityMapper.increaseViewCount(postId);
    }

    // === 댓글 ===

    // 댓글 작성
    public void insertComment(Long postId, Long userId, CommunityDto.CommentCreateRequest req) {
        commentMapper.insertComment(postId, userId, req);
    }

    // 댓글 수정
    public int updateCommentByWriter(Long commentId, Long userId, CommunityDto.CommentUpdateRequest req) {
        return commentMapper.updateCommentByWriter(commentId, userId, req);
    }

    // 댓글 삭제
    public int deleteCommentByWriter(Long commentId, Long userId) {
        return commentMapper.deleteCommentByWriter(commentId, userId);
    }

    // 댓글 목록
    public List<CommunityDto.CommentResponse> findCommentByPostId(Long postId) {
        return commentMapper.findCommentByPostId(postId);
    }
}