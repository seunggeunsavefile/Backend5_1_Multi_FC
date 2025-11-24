package com.multi.backend5_1_multi_fc.schedule.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduleDto {

    @Data
    public static class DayItem {
        private Long scheduleId;
        private String title;          // 일정명
        private String scheduleType;   // 개인, 경기
        private LocalDate scheduleDate;
        private LocalTime scheduleTime;
        //추가
        private String content; // 일정 내용
        private Long matchId; // 매칭방 match_room_id)
    }

    @Data
    public static class PersonalCreateReq {
        // 개인 일정 생성
        private LocalDate scheduleDate;
        private LocalTime scheduleTime;
        private String title;
        private String content;
    }

    @Data
    public static class PersonalUpdateReq {
        // 개인일정 수정
        private LocalDate scheduleDate;
        private LocalTime scheduleTime;
        private String title;
        private String content;
    }
}
