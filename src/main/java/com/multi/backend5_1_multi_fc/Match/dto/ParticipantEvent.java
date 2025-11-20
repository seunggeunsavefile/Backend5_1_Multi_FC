// Websocket 전송용 이벤트 DTO
package com.multi.backend5_1_multi_fc.match.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantEvent {
    private Long roomId;       // 경기방 ID
    private Long userId;       // 참여한 사용자 ID
    private String action;     // "JOIN" or "LEAVE"
    private int currentCount;  // 현재 참여자 수
}
