package com.multi.backend5_1_multi_fc.review.dto;

import lombok.Getter;
import lombok.Setter;

// 후기 목록 조회 시 사용할 DTO (닉네임, 작성일 등 포함)
@Getter
@Setter
public class ReviewDto {
    private Long reviewId;
    private Long stadiumId;
    private Long userId;
    private String nickname; // User 테이블에서 조인
    private Integer rating;
    private String content;
    private String createdAt; // TIMESTAMP -> String 변환 (YYYY-MM-DD 형식)
}