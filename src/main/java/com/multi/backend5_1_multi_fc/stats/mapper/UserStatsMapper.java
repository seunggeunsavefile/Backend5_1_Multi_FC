package com.multi.backend5_1_multi_fc.stats.mapper;

import com.multi.backend5_1_multi_fc.stats.dto.UserStatsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserStatsMapper {

    Long findUserIdByUsername(@Param("username") String username);

    // 올해 매칭 승인 / 전체 신청 수
    UserStatsDto.MatchingRateRaw findMatchingRate(
            @Param("userId") Long userId,
            @Param("year") int year
    );
}
