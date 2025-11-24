package com.multi.backend5_1_multi_fc.match.controller;

import com.multi.backend5_1_multi_fc.match.dto.MatchRoomCreateReq;
import com.multi.backend5_1_multi_fc.match.dto.MatchRoomDto;
import com.multi.backend5_1_multi_fc.match.service.MatchRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matchrooms")
@RequiredArgsConstructor
public class MatchRoomController {

    private final MatchRoomService matchRoomService;

    @GetMapping("/{roomId}")
    public MatchRoomDto getMatchById(@PathVariable Long roomId) {
        return matchRoomService.findById(roomId);
    }

    @PostMapping
    public MatchRoomDto createMatch(@RequestBody MatchRoomCreateReq req) {
        return matchRoomService.create(req);
    }

    @GetMapping("/stadium/{stadiumId}")
    public List<MatchRoomDto> getByStadium(@PathVariable Long stadiumId) {
        return matchRoomService.findByStadium(stadiumId);
    }

    /** ⭐️ [추가] 내가 참가/생성한 경기 목록 조회 API */
    @GetMapping("/my-schedules")
    public List<MatchRoomDto> getMySchedules(@RequestParam Long userId) {
        // 실제 운영 환경에서는 SecurityContext에서 userId를 가져와야 합니다.
        return matchRoomService.findByUserId(userId);
    }

    @PostMapping("/{roomId}/close")
    public void closeMatch(@PathVariable Long roomId) {
        matchRoomService.closeMatch(roomId);
    }
}