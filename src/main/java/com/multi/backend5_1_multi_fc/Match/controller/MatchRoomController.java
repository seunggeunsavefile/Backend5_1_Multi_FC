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

    /** ✅ [신규] 경기 마감 */
    @PostMapping("/{roomId}/close")
    public void closeMatch(@PathVariable Long roomId) {
        matchRoomService.closeMatch(roomId);
    }
}