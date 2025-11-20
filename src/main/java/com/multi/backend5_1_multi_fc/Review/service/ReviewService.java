package com.multi.backend5_1_multi_fc.review.service;

import com.multi.backend5_1_multi_fc.review.dto.ReviewCreateReq;
import com.multi.backend5_1_multi_fc.review.dto.ReviewDto;
import com.multi.backend5_1_multi_fc.review.mapper.ReviewMapper;
import com.multi.backend5_1_multi_fc.match.mapper.MatchRoomMapper;
import com.multi.backend5_1_multi_fc.match.mapper.MatchParticipantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewMapper reviewMapper;
    // 검증을 위해 주입된 매퍼들 (Match 관련 로직 검증에 필요)
    private final MatchRoomMapper matchRoomMapper;
    private final MatchParticipantMapper participantMapper;


    /**
     * 후기 작성 (경기에 참여하고 마감된 사용자만 허용)
     */
    @Transactional
    public void createReview(ReviewCreateReq req) {
        if (req.getRating() == null || req.getRating() < 1 || req.getRating() > 5) {
            throw new IllegalArgumentException("평점은 1에서 5 사이여야 합니다.");
        }

        // 1. 해당 유저가 해당 구장(stadiumId)에서 완료된 경기에 참가했는지 확인 (Mapper 사용)
        boolean hasCompletedMatch = reviewMapper.checkUserCompletedMatch(req.getUserId(), req.getStadiumId());

        if (!hasCompletedMatch) {
            throw new SecurityException("경기가 마감되었거나, 해당 구장에서 경기한 기록이 없어 후기 작성 권한이 없습니다.");
        }

        // 2. 이미 후기를 작성했는지 확인 (Mapper 사용)
        if (reviewMapper.hasUserAlreadyReviewed(req.getUserId(), req.getStadiumId())) {
            throw new IllegalStateException("이미 해당 구장에 대한 후기를 작성했습니다.");
        }

        reviewMapper.insert(req);
    }

    /**
     * 구장별 후기 목록 조회
     */
    public List<ReviewDto> getReviewsByStadium(Long stadiumId) {
        return reviewMapper.findByStadiumId(stadiumId);
    }

    // ⭐⭐⭐ [수정됨] ReviewController에서 호출하는 메서드입니다. ⭐⭐⭐
    /**
     * 후기 작성이 필요한 구장 ID 목록 반환 (메인 페이지 알림용)
     */
    public List<Long> getPendingReviewStadiums(Long userId) {
        return reviewMapper.findPendingReviewStadiumIds(userId);
    }
    // ⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐

    /**
     * 후기 수정
     */
    @Transactional
    public void updateReview(Long reviewId, Long userId, Integer rating, String content) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("평점은 1에서 5 사이여야 합니다.");
        }

        int updatedRows = reviewMapper.update(reviewId, userId, rating, content);

        if (updatedRows == 0) {
            throw new SecurityException("해당 후기를 수정할 권한이 없거나 후기를 찾을 수 없습니다.");
        }
    }

    /**
     * 후기 삭제
     */
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        int deletedRows = reviewMapper.delete(reviewId, userId);

        if (deletedRows == 0) {
            throw new SecurityException("해당 후기를 삭제할 권한이 없거나 후기를 찾을 수 없습니다.");
        }
    }
}