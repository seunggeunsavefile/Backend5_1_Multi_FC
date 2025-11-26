package com.multi.backend5_1_multi_fc.community.mapper;

import com.multi.backend5_1_multi_fc.community.dto.CommunityDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    // 댓글 등록
    void insertComment(@Param("postId") Long postId,
                       @Param("userId") Long userId,
                       @Param("req")CommunityDto.CommentCreateRequest req);


    // 댓글 수정
    int updateCommentByWriter(@Param("commentId") Long commentId,
                              @Param("userId") Long userId,
                              @Param("req") CommunityDto.CommentUpdateRequest req);

    // 댓글 삭제
    int deleteCommentByWriter(@Param("commentId") Long commentId,
                              @Param("userId") Long userId);

    // 특정 게시글의 전체 댓글 조회
    List<CommunityDto.CommentResponse> findCommentByPostId(@Param("postId") Long postId);

    // 댓글 작성자 찾기
    Long findWriterIdByCommentId(@Param("commentId") Long commentId);

    // 마지막 생성된 comment_id
    Long findLastInsertedCommentId();

}