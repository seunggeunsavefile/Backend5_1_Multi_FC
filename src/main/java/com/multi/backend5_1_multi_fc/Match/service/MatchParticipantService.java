package com.multi.backend5_1_multi_fc.match.service;

import com.multi.backend5_1_multi_fc.match.dto.MatchRoomDto;
import com.multi.backend5_1_multi_fc.match.dto.ParticipantDto;
import com.multi.backend5_1_multi_fc.match.mapper.MatchParticipantMapper;
import com.multi.backend5_1_multi_fc.match.mapper.MatchRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchParticipantService {

    private final MatchParticipantMapper participantMapper;
    private final MatchRoomMapper matchRoomMapper;
    private final MatchEventPublisher eventPublisher;

    /** 참가 신청 */
    @Transactional
    public void join(Long roomId, Long userId) {
        MatchRoomDto room = matchRoomMapper.findById(roomId);
        // 마감된 경기면 참가 불가
        if ("CLOSED".equals(room.getStatus())) {
            throw new IllegalStateException("이미 마감된 경기입니다.");
        }
        if (room.getHostId().equals(userId)) return;

        if (participantMapper.existsByRoomAndUser(roomId, userId) == 0) {
            participantMapper.insert(roomId, userId);
            int currentCount = participantMapper.countByRoom(roomId);
            eventPublisher.publishParticipantEvent(roomId, userId, "JOIN", currentCount);
        }
    }

    /** 참가 취소 */
    @Transactional
    public void cancel(Long roomId, Long userId) {
        MatchRoomDto room = matchRoomMapper.findById(roomId);
        if (room.getHostId().equals(userId)) return;

        if (participantMapper.existsByRoomAndUser(roomId, userId) > 0) {
            participantMapper.delete(roomId, userId);
            int currentCount = participantMapper.countByRoom(roomId);
            eventPublisher.publishParticipantEvent(roomId, userId, "LEAVE", currentCount);
        }
    }

    /** ✅ [신규] 참가자 수락 */
    @Transactional
    public void approve(Long roomId, Long userId) {
        participantMapper.updateStatus(roomId, userId, "확정");
        int currentCount = participantMapper.countByRoom(roomId);
        // UPDATE 액션으로 알림 전송
        eventPublisher.publishParticipantEvent(roomId, userId, "UPDATE", currentCount);
    }

    /** ✅ [신규] 참가자 강퇴/거절 */
    @Transactional
    public void reject(Long roomId, Long userId) {
        participantMapper.delete(roomId, userId);
        int currentCount = participantMapper.countByRoom(roomId);
        // LEAVE 액션으로 알림 전송 (목록에서 사라짐)
        eventPublisher.publishParticipantEvent(roomId, userId, "LEAVE", currentCount);
    }

    public List<Long> getParticipants(Long roomId) {
        return participantMapper.findUserIdsByRoom(roomId);
    }

    public List<ParticipantDto> getParticipantsWithHost(Long roomId) {
        List<ParticipantDto> allParticipants = new ArrayList<>();

        ParticipantDto host = participantMapper.findHostInfo(roomId);
        if (host != null) allParticipants.add(host);

        List<ParticipantDto> players = participantMapper.findParticipantsInfo(roomId);
        if (players != null) allParticipants.addAll(players);

        return allParticipants;
    }
}