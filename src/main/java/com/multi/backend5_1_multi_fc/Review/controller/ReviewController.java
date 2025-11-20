package com.multi.backend5_1_multi_fc.review.controller;

import com.multi.backend5_1_multi_fc.review.dto.ReviewCreateReq;
import com.multi.backend5_1_multi_fc.review.dto.ReviewDto;
import com.multi.backend5_1_multi_fc.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /** POST /api/reviews : 후기 등록 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createReview(@RequestBody ReviewCreateReq req) {
        reviewService.createReview(req);
    }

    /** GET /api/reviews/stadium/{stadiumId} : 구장별 후기 목록 조회 */
    @GetMapping("/stadium/{stadiumId}")
    public List<ReviewDto> getReviewsByStadium(@PathVariable Long stadiumId) {
        return reviewService.getReviewsByStadium(stadiumId);
    }

    // ⭐⭐⭐ [추가] 후기 작성이 필요한 구장 ID 목록 API ⭐⭐⭐
    @GetMapping("/pending-stadiums")
    public List<Long> getPendingReviewStadiums(@RequestParam Long userId) {
        // ReviewService.java에서 getPendingReviewStadiums 메서드를 호출합니다.
        return reviewService.getPendingReviewStadiums(userId);
    }
    // ⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐

    /** PUT /api/reviews/{reviewId} : 후기 수정 (본인만 가능) */
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody ReviewCreateReq req) {
        Long userId = req.getUserId(); // 프론트에서 userId를 같이 보낸다고 가정
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보가 필요합니다.");
        }

        try {
            reviewService.updateReview(reviewId, userId, req.getRating(), req.getContent());
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /** DELETE /api/reviews/{reviewId} : 후기 삭제 (본인만 가능) */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, @RequestParam Long userId) {
        try {
            reviewService.deleteReview(reviewId, userId);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("후기를 찾을 수 없습니다.");
        }
    }
}