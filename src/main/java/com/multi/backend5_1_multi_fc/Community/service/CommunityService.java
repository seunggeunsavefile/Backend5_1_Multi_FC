package com.multi.backend5_1_multi_fc.community.service;

import com.multi.backend5_1_multi_fc.community.dao.CommunityDao;
import com.multi.backend5_1_multi_fc.community.dto.CommunityDto;
import com.multi.backend5_1_multi_fc.community.exception.CommunityException;
import com.multi.backend5_1_multi_fc.user.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {
    private final CommunityDao communityDao;
    private final S3Service s3Service;

    // XSS 방지용 기본 escape
    private String sanitize(String input) {
        if (input == null) return null;
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    // === 게시글 ===
    @Transactional
    public void createPost(String username, String role,
                           CommunityDto.PostCreateRequest req,
                           MultipartFile image) {

        if (!"ROLE_ADMIN".equals(role) && !"자유게시판".equals(req.getCategory())) {
            throw new CommunityException("자유게시판만 글 작성이 가능합니다.");
        }

        Long userId = communityDao.findUserIdByUsername(username);
        if (userId == null) {
            throw new CommunityException("사용자를 찾을 수 없습니다.");
        }

        // 제목/내용 sanitize
        req.setTitle(sanitize(req.getTitle()));
        req.setContent(sanitize(req.getContent()));

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = s3Service.uploadFile(image);
            } catch (IOException e) {
                throw new CommunityException("이미지 업로드에 실패했습니다.");
            }
        }

        communityDao.insertPost(userId, req, imageUrl);
    }

    // 카테고리별 게시글 가져오기
    public List<CommunityDto.PostListResponse> getPostsByCategory(String category) {
        return communityDao.findPostByCategory(category);
    }

    // 게시글 상세
    @Transactional
    public CommunityDto.PostDetailResponse getPostDetail(Long postId) {
        // 조회수 증가
        communityDao.increaseViewCount(postId);

        CommunityDto.PostDetailResponse detail = communityDao.findPostDetail(postId);
        if (detail == null) {
            throw new CommunityException("게시글을 찾을 수 없습니다.");
        }

        // 댓글 리스트
        List<CommunityDto.CommentResponse> comments =
                communityDao.findCommentByPostId(postId);
        detail.setComments(comments);

        return detail;
    }

    // 게시글 수정
    @Transactional
    public void updatePost(Long postId, String username,
                           CommunityDto.PostUpdateRequest req) {

        Long userId = communityDao.findUserIdByUsername(username);

        // 수정할 때도 sanitize
        req.setTitle(sanitize(req.getTitle()));
        req.setContent(sanitize(req.getContent()));

        int updated = communityDao.updatePostByWriter(postId, userId, req);
        if (updated == 0) {
            throw new CommunityException("작성자만 게시글을 수정할 수 있습니다.");
        }
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, String username) {
        Long userId = communityDao.findUserIdByUsername(username);
        int deleted = communityDao.deletePostByWriter(postId, userId);
        if (deleted == 0) {
            throw new CommunityException("작성자만 게시글을 삭제할 수 있습니다.");
        }
    }

    // === 댓글 ===

    // 댓글 생성
    @Transactional
    public void createComment(Long postId, String username,
                              CommunityDto.CommentCreateRequest req) {

        Long userId = communityDao.findUserIdByUsername(username);
        if (userId == null) {
            throw new CommunityException("사용자를 찾을 수 없습니다.");
        }

        req.setContent(sanitize(req.getContent()));
        communityDao.insertComment(postId, userId, req);
    }

    // 댓글 수정
    @Transactional
    public void updateComment(Long commentId, String username,
                              CommunityDto.CommentUpdateRequest req) {

        Long userId = communityDao.findUserIdByUsername(username);
        if (userId == null) {
            throw new CommunityException("사용자를 찾을 수 없습니다.");
        }

        req.setContent(sanitize(req.getContent()));

        int updated = communityDao.updateCommentByWriter(commentId, userId, req);
        if (updated == 0) {
            throw new CommunityException("작성자만 댓글을 수정할 수 있습니다.");
        }
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, String username) {

        Long userId = communityDao.findUserIdByUsername(username);
        if (userId == null) {
            throw new CommunityException("사용자를 찾을 수 없습니다.");
        }

        int deleted = communityDao.deleteCommentByWriter(commentId, userId);
        if (deleted == 0) {
            throw new CommunityException("작성자만 댓글을 삭제할 수 있습니다.");
        }
    }
}
