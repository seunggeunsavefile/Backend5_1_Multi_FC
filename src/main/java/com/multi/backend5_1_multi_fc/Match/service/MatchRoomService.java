package com.multi.backend5_1_multi_fc.match.service;

import com.multi.backend5_1_multi_fc.match.dto.MatchRoomCreateReq;
import com.multi.backend5_1_multi_fc.match.dto.MatchRoomDto;
import com.multi.backend5_1_multi_fc.match.mapper.MatchRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchRoomService {

    private final MatchRoomMapper matchRoomMapper;
    private final MatchEventPublisher eventPublisher;

    @Transactional
    public MatchRoomDto create(MatchRoomCreateReq req) {
        matchRoomMapper.insert(req);
        MatchRoomDto newRoom = matchRoomMapper.findById(req.getRoomId());
        eventPublisher.publishNewMatchForStadium(newRoom.getStadiumId(), newRoom);
        return newRoom;
    }

    public MatchRoomDto findById(Long roomId) {
        return matchRoomMapper.findById(roomId);
    }

    public List<MatchRoomDto> findByStadium(Long stadiumId) {
        return matchRoomMapper.findByStadium(stadiumId);
    }

    // ⭐️ [추가] 내가 참가/생성한 경기 목록 조회
    public List<MatchRoomDto> findByUserId(Long userId) {
        return matchRoomMapper.findByUserId(userId);
    }

    @Transactional
    public void closeMatch(Long roomId) {
        matchRoomMapper.updateStatus(roomId, "CLOSED");
    }
}