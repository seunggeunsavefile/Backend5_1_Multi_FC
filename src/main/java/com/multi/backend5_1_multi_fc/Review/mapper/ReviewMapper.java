package com.multi.backend5_1_multi_fc.review.mapper;

import com.multi.backend5_1_multi_fc.review.dto.ReviewCreateReq;
import com.multi.backend5_1_multi_fc.review.dto.ReviewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    /** 새로운 후기 삽입 */
    void insert(ReviewCreateReq req);

    /** 특정 구장의 후기 목록 조회 */
    List<ReviewDto> findByStadiumId(Long stadiumId);

    // ⭐⭐⭐ [추가] 후기 작성 권한 검증 메서드 ⭐⭐⭐
    boolean checkUserCompletedMatch(@Param("userId") Long userId, @Param("stadiumId") Long stadiumId);

    // ⭐⭐⭐ [추가] 후기 중복 작성 검증 메서드 ⭐⭐⭐
    boolean hasUserAlreadyReviewed(@Param("userId") Long userId, @Param("stadiumId") Long stadiumId);

    // ⭐⭐⭐ [추가] 후기 작성이 필요한 구장 ID 목록 조회 메서드 ⭐⭐⭐
    List<Long> findPendingReviewStadiumIds(@Param("userId") Long userId);

    // --- 기존 update/delete 메서드 ---

    /** 후기 수정 (ID와 USER_ID로 검증) */
    int update(@Param("reviewId") Long reviewId,
               @Param("userId") Long userId,
               @Param("rating") Integer rating,
               @Param("content") String content);

    /** 후기 삭제 (ID와 USER_ID로 검증) */
    int delete(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
}