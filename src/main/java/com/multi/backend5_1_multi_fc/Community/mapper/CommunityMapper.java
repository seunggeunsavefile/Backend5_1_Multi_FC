package com.multi.backend5_1_multi_fc.community.mapper;

import com.multi.backend5_1_multi_fc.community.dto.CommunityDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityMapper {
    // username → userId 조회
    Long findUserIdByUsername(@Param("username") String username);

    // 글 작성
    void insertPost(@Param("userId")Long userId,
                    @Param("req")CommunityDto.PostCreateRequest req,
                    @Param("imageUrl") String imageUrl);

    // 카테고리별 목록
    List<CommunityDto.PostListResponse> findPostByCategory(
            @Param("category") String category
    );

    // 게시글 상세
    CommunityDto.PostDetailResponse findPostDetail(
            @Param("postId") Long postId
    );

    // 게시글 수정
    int updatePostByWriter(@Param("postId")Long postId,
                           @Param("userId") Long userId,
                           @Param("req") CommunityDto.PostUpdateRequest req);

    // 게시글 삭제
    int deletePostByWriter(@Param("postId") Long postId,
                           @Param("userId") Long userId);

    // 조회수 증가
    void increaseViewCount(@Param("postId") Long postId);
}
