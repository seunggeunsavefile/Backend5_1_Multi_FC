package com.multi.backend5_1_multi_fc.schedule.mapper;

import com.multi.backend5_1_multi_fc.schedule.dto.ScheduleDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper
public interface ScheduleMapper {

    // 사용자 조회 (username -> user_id)
    Long findUserIdByUsername(@Param("username") String username);

    // 개인 일정 CUD
    int insertPersonal(@Param("userId") Long userId,
                       @Param("req") ScheduleDto.PersonalCreateReq req);

    int updatePersonal(@Param("userId") Long userId,
                       @Param("scheduleId") Long scheduleId,
                       @Param("req") ScheduleDto.PersonalUpdateReq req);

    int deletePersonal(@Param("userId") Long userId,
                       @Param("scheduleId") Long scheduleId);

    // 달력: 해당 날짜 가장 빠른 2개
    List<ScheduleDto.DayItem> findTop2ByDate(@Param("userId") Long userId,
                                             @Param("date") LocalDate date);

    // 해당 날짜 전체
    List<ScheduleDto.DayItem> findAllByDate(@Param("userId") Long userId,
                                            @Param("date") LocalDate date);

    // 메인 오늘 현재시각 이후 + 내일부터는 하루 전체 중 빠른 순으로 5개
    List<ScheduleDto.DayItem> findUpcoming7d(@Param("userId") Long userId,
                                             @Param("today") LocalDate today,
                                             @Param("nowTime") LocalTime nowTime,
                                             @Param("tomorrow") LocalDate tomorrow,
                                             @Param("sixDaysLater") LocalDate sixDaysLater);

    // 경기 자동 연동
    int upsertApprovedMatches(@Param("userId") Long userId);

    // 이번 달 전체 일정 조회
    List<ScheduleDto.DayItem> findAllBetween(@Param("userId") Long userId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
}
