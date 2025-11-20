// Websocket 이벤트 전송 기능
package com.multi.backend5_1_multi_fc.match.service;

import com.multi.backend5_1_multi_fc.match.dto.MatchRoomDto;
import com.multi.backend5_1_multi_fc.match.dto.ParticipantEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    // ★ 필요하다면 경기 생성시도 사용 가능
    public void publishNewMatchForStadium(Long stadiumId, MatchRoomDto room) {
        messagingTemplate.convertAndSend("/topic/matches/" + stadiumId, room);
    }

    // ★ 참여자 변경 이벤트
    public void publishParticipantEvent(Long roomId, Long userId, String action, int currentCount) {
        ParticipantEvent event = new ParticipantEvent(roomId, userId, action, currentCount);
        messagingTemplate.convertAndSend("/topic/matchroom/" + roomId + "/participants", event);
    }
}
