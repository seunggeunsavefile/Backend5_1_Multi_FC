package com.multi.backend5_1_multi_fc.schedule.repository;

import com.multi.backend5_1_multi_fc.schedule.dao.ScheduleDao;
import com.multi.backend5_1_multi_fc.schedule.dto.ScheduleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduleRepo {

    private final ScheduleDao dao;

    public Long findUserIdByUsername(String username) {
        return dao.findUserIdByUsername(username);
    }

    public void insertPersonal(Long userId, ScheduleDto.PersonalCreateReq req) {
        dao.insertPersonal(userId, req);
    }

    public boolean updatePersonal(Long userId, Long scheduleId, ScheduleDto.PersonalUpdateReq req) {
        return dao.updatePersonal(userId, scheduleId, req) > 0;
    }

    public boolean deletePersonal(Long userId, Long scheduleId) {
        return dao.deletePersonal(userId, scheduleId) > 0;
    }

    public List<ScheduleDto.DayItem> findTop2ByDate(Long userId, LocalDate date) {
        return dao.findTop2ByDate(userId, date);
    }

    public List<ScheduleDto.DayItem> findAllByDate(Long userId, LocalDate date) {
        return dao.findAllByDate(userId, date);
    }

    public List<ScheduleDto.DayItem> findUpcoming7d(Long userId,
                                                    LocalDate today,
                                                    LocalTime nowTime,
                                                    LocalDate tomorrow,
                                                    LocalDate sixDaysLater) {
        return dao.findUpcoming7d(userId, today, nowTime, tomorrow, sixDaysLater);
    }

    public int upsertApprovedMatches(Long userId) {
        return dao.upsertApprovedMatches(userId);
    }

    public List<ScheduleDto.DayItem> findAllBetween(Long userId,
                                                    LocalDate startDate,
                                                    LocalDate endDate) {
        return dao.findAllBetween(userId, startDate, endDate);
    }
}
