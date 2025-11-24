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

    @Transactional
    // ⭐️ [수정] position 인자를 제거하고 userId만 받습니다.
    public void join(Long roomId, Long userId) {
        MatchRoomDto room = matchRoomMapper.findById(roomId);

        // 1. User 테이블에서 기본 position 정보 조회
        String defaultPosition = participantMapper.findUserDefaultPosition(userId);

        if (defaultPosition == null || defaultPosition.trim().isEmpty()) {
            defaultPosition = "미정"; // DB에 포지션 값이 없을 경우 기본값 설정
        }

        if ("CLOSED".equals(room.getStatus())) {
            throw new IllegalStateException("이미 마감된 경기입니다.");
        }
        if (room.getHostId().equals(userId)) return;

        if (participantMapper.existsByRoomAndUser(roomId, userId) == 0) {
            // 2. 기본 포지션을 DB에 저장
            participantMapper.insert(roomId, userId, defaultPosition);

            int currentCount = participantMapper.countByRoom(roomId);
            eventPublisher.publishParticipantEvent(roomId, userId, "JOIN", currentCount);
        }
    }

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

    @Transactional
    public void approve(Long roomId, Long userId) {
        participantMapper.updateStatus(roomId, userId, "확정");
        int currentCount = participantMapper.countByRoom(roomId);
        eventPublisher.publishParticipantEvent(roomId, userId, "UPDATE", currentCount);
    }

    @Transactional
    public void reject(Long roomId, Long userId) {
        participantMapper.delete(roomId, userId);
        int currentCount = participantMapper.countByRoom(roomId);
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