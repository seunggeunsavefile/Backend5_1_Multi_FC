package com.multi.backend5_1_multi_fc.stats.service;

import com.multi.backend5_1_multi_fc.stats.dto.UserStatsDto;
import com.multi.backend5_1_multi_fc.stats.mapper.UserStatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserStatsService {

    private final UserStatsMapper userStatsMapper;

    // 로그인한 사용자의 매칭률을 계산해서 반환
    public UserStatsDto.MatchingRateResponse getMyMatchingRate(String username) {

        // username 으로 User 테이블에서 user_id 조회
        Long userId = userStatsMapper.findUserIdByUsername(username);
        // 해당 username이 없으면 매칭률 0으로 채운 빈 응답 보냠
        if (userId == null) {
            // 유저가 없으면 0으로 리턴
            UserStatsDto.MatchingRateResponse empty = new UserStatsDto.MatchingRateResponse();
            empty.setApprovedCount(0);
            empty.setTotalRequests(0);
            empty.setMatchingRate(0);
            return empty;
        }

        //기준 연도 계산 (올해 기준 사용)
        int year = LocalDate.now().getYear();

        UserStatsDto.MatchingRateRaw raw =
                userStatsMapper.findMatchingRate(userId, year);

        int approved = (raw != null) ? raw.getApproved() : 0;
        int total = (raw != null) ? raw.getTotal() : 0;

        // 매칭률 계산
        int rate = 0;
        if (total > 0) {
            rate = (int) Math.round((approved * 100.0) / total);
        }

        UserStatsDto.MatchingRateResponse response = new UserStatsDto.MatchingRateResponse();
        response.setApprovedCount(approved);
        response.setTotalRequests(total);
        response.setMatchingRate(rate);

        return response;
    }
}
