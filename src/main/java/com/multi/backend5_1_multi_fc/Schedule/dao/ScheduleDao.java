package com.multi.backend5_1_multi_fc.schedule.dao;

import com.multi.backend5_1_multi_fc.schedule.dto.ScheduleDto;
import com.multi.backend5_1_multi_fc.schedule.mapper.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduleDao {

    private final ScheduleMapper mapper;

    public Long findUserIdByUsername(String username) {
        return mapper.findUserIdByUsername(username);
    }

    public int insertPersonal(Long userId, ScheduleDto.PersonalCreateReq req) {
        return mapper.insertPersonal(userId, req);
    }

    public int updatePersonal(Long userId, Long scheduleId, ScheduleDto.PersonalUpdateReq req) {
        return mapper.updatePersonal(userId, scheduleId, req);
    }

    public int deletePersonal(Long userId, Long scheduleId) {
        return mapper.deletePersonal(userId, scheduleId);
    }

    public List<ScheduleDto.DayItem> findTop2ByDate(Long userId, LocalDate date) {
        return mapper.findTop2ByDate(userId, date);
    }

    public List<ScheduleDto.DayItem> findAllByDate(Long userId, LocalDate date) {
        return mapper.findAllByDate(userId, date);
    }

    public List<ScheduleDto.DayItem> findUpcoming7d(Long userId,
                                                    LocalDate today,
                                                    LocalTime nowTime,
                                                    LocalDate tomorrow,
                                                    LocalDate sixDaysLater) {
        return mapper.findUpcoming7d(userId, today, nowTime, tomorrow, sixDaysLater);
    }

    public int upsertApprovedMatches(Long userId) {
        return mapper.upsertApprovedMatches(userId);
    }

    public List<ScheduleDto.DayItem> findAllBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return mapper.findAllBetween(userId, startDate, endDate);
    }
}
