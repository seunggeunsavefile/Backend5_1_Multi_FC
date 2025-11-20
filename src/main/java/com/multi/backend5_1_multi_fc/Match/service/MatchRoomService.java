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

    /** ✅ [신규] 경기 모집 마감 (매칭 확정) */
    @Transactional
    public void closeMatch(Long roomId) {
        matchRoomMapper.updateStatus(roomId, "CLOSED");

        // 마감 이벤트를 발행하여 보고 있는 사람들에게 알림 (선택 사항)
        // 여기서는 단순히 상태만 바꾸지만, 필요하면 WebSocket 메시지를 보낼 수도 있습니다.
    }
}