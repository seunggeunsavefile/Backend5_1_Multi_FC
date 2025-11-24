
package com.multi.backend5_1_multi_fc.schedule.controller;

import com.multi.backend5_1_multi_fc.schedule.dto.ScheduleDto;
import com.multi.backend5_1_multi_fc.schedule.exception.ScheduleException;
import com.multi.backend5_1_multi_fc.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // JWT 필터가 넣어준 인증 정보에서 username 가져오기
    private String getCurrentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ScheduleException("로그인이 필요한 요청입니다.");
        }

        return auth.getName();
    }
    // 개인 일정 추가
    @PostMapping("/personal")
    public ResponseEntity<Void> addPersonal(
            @RequestBody ScheduleDto.PersonalCreateReq req
    ) {
        String username = getCurrentUsername();
        scheduleService.addPersonal(username, req);
        return ResponseEntity.ok().build();
    }

    // 개인 일정 수정
    @PutMapping("/personal/{scheduleId}")
    public ResponseEntity<Void> updatePersonal(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleDto.PersonalUpdateReq req
    ) {
        String username = getCurrentUsername();
        scheduleService.updatePersonal(username, scheduleId, req);
        return ResponseEntity.ok().build();
    }

    // 개인 일정 삭제
    @DeleteMapping("/personal/{scheduleId}")
    public ResponseEntity<Void> deletePersonal(
            @PathVariable Long scheduleId
    ) {
        String username = getCurrentUsername();
        scheduleService.deletePersonal(username, scheduleId);
        return ResponseEntity.ok().build();
    }

    // 특정 달 전체 일정 조회
    @GetMapping("/month")
    public ResponseEntity<List<ScheduleDto.DayItem>> getMonthSchedules(
            @RequestParam int year,
            @RequestParam int month
    ) {
        String username = getCurrentUsername();
        // 경기 자동 연동
        scheduleService.syncApprovedMatches(username);

        YearMonth ym = YearMonth.of(year, month);
        List<ScheduleDto.DayItem> list = scheduleService.allByMonth(username, ym);

        return ResponseEntity.ok(list);
    }

    // 특정 날짜 전체 일정 조회
    @GetMapping("/day")
    public ResponseEntity<List<ScheduleDto.DayItem>> getDaySchedules(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        String username = getCurrentUsername();
        // 1) 경기 일정 먼저 동기화 (Match → Schedule)
        scheduleService.syncApprovedMatches(username);

        // 2) 그 날짜의 전체 일정 조회 (개인 일정 + 경기 일정 모두 포함)
        List<ScheduleDto.DayItem> result = scheduleService.allByDate(username, date);
        return ResponseEntity.ok(result);
    }

    // 메인 페이지용 - 7일 이내 일정 조회
    @GetMapping("/upcoming")
    public ResponseEntity<List<ScheduleDto.DayItem>> getUpcoming7Days() {
        String username = getCurrentUsername();
        System.out.println("[DEBUG] /api/schedule/upcoming username = " + username);

        scheduleService.syncApprovedMatches(username);

        List<ScheduleDto.DayItem> result = scheduleService.upcoming7Days(username);
        return ResponseEntity.ok(result);
    }
}
