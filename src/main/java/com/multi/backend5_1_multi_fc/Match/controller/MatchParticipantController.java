package com.multi.backend5_1_multi_fc.match.controller;

import com.multi.backend5_1_multi_fc.match.dto.JoinReq;
import com.multi.backend5_1_multi_fc.match.dto.ParticipantDto;
import com.multi.backend5_1_multi_fc.match.service.MatchParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matchrooms")
@RequiredArgsConstructor
public class MatchParticipantController {

    private final MatchParticipantService participantService;

    @PostMapping("/{roomId}/join")
    public void join(@PathVariable Long roomId, @RequestBody JoinReq req) {
        participantService.join(roomId, req.getUserId());
    }

    @PostMapping("/{roomId}/cancel")
    public void cancel(@PathVariable Long roomId, @RequestBody JoinReq req) {
        participantService.cancel(roomId, req.getUserId());
    }

    /** ✅ [신규] 참가 승인 */
    @PostMapping("/{roomId}/approve")
    public void approve(@PathVariable Long roomId, @RequestBody JoinReq req) {
        participantService.approve(roomId, req.getUserId());
    }

    /** ✅ [신규] 참가 강퇴 */
    @PostMapping("/{roomId}/reject")
    public void reject(@PathVariable Long roomId, @RequestBody JoinReq req) {
        participantService.reject(roomId, req.getUserId());
    }

    @GetMapping("/{roomId}/participants")
    public List<Long> getParticipants(@PathVariable Long roomId) {
        return participantService.getParticipants(roomId);
    }

    @GetMapping("/{roomId}/participants/all")
    public List<ParticipantDto> getAllParticipants(@PathVariable Long roomId) {
        return participantService.getParticipantsWithHost(roomId);
    }
}