package com.multi.backend5_1_multi_fc.stats.dto;

import lombok.Data;

public class UserStatsDto {

    @Data
    public static class MatchingRateRaw {
        private int approved;   // 승인된 신청 수
        private int total;      // 전체 신청 수
    }

    @Data
    public static class MatchingRateResponse {
        private int approvedCount;   // 승인된 경기 수
        private int totalRequests;   // 총 신청 경기 수
        private int matchingRate;    // 매칭률
    }
}
