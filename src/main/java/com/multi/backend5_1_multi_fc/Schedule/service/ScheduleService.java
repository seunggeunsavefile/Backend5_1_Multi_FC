package com.multi.backend5_1_multi_fc.schedule.service;

import com.multi.backend5_1_multi_fc.schedule.dto.ScheduleDto;
import com.multi.backend5_1_multi_fc.schedule.exception.ScheduleException;
import com.multi.backend5_1_multi_fc.schedule.repository.ScheduleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepo repo;
    //모든 시간 날짜 계산 고정
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    // username -> user_id 변환
    private Long userIdOf(String username) {
        Long userId = repo.findUserIdByUsername(username);
        if (userId == null) throw new ScheduleException("사용자를 찾을 수 없습니다.");
        return userId;
    }

    @Transactional
    //개인 일정 생성
    public void addPersonal(String username, ScheduleDto.PersonalCreateReq req) {
        repo.insertPersonal(userIdOf(username), req);
    }

    @Transactional
    //개인 일정 수정
    public void updatePersonal(String username, Long scheduleId, ScheduleDto.PersonalUpdateReq req) {
        boolean ok = repo.updatePersonal(userIdOf(username), scheduleId, req);
        if (!ok) throw new ScheduleException("일정이 없거나 수정 권한이 없습니다.");
    }

    @Transactional
    //개인 일정 삭제
    public void deletePersonal(String username, Long scheduleId) {
        boolean ok = repo.deletePersonal(userIdOf(username), scheduleId);
        if (!ok) throw new ScheduleException("일정이 없거나 삭제 권한이 없습니다.");
    }

    //이번달 일정
    public List<ScheduleDto.DayItem> allByMonth(String username, YearMonth yearMonth) {
        Long userId = userIdOf(username);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end   = yearMonth.atEndOfMonth();
        return repo.findAllBetween(userId, start, end);
    }

    //달력 칸에 빠른 일정 2개 표시
    public List<ScheduleDto.DayItem> top2ByDate(String username, LocalDate date) {
        return repo.findTop2ByDate(userIdOf(username), date);
    }

    //특정 날짜 전체 일정
    public List<ScheduleDto.DayItem> allByDate(String username, LocalDate date) {
        return repo.findAllByDate(userIdOf(username), date);
    }

    //메인 일정표시
    public List<ScheduleDto.DayItem> upcoming7Days(String username) {
        Long userId = userIdOf(username);
        LocalDate today = LocalDate.now(KST);
        LocalTime now   = LocalTime.now(KST);
        LocalDate tomorrow     = today.plusDays(1);
        LocalDate sixDaysLater = today.plusDays(6); // 오늘 포함 총 7일
        return repo.findUpcoming7d(userId, today, now, tomorrow, sixDaysLater);
    }

    //경기 자동연동
    @Transactional
    public int syncApprovedMatches(String username) {
        return repo.upsertApprovedMatches(userIdOf(username));
    }


}
