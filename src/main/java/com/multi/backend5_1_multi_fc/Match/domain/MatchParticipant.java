// 경기 참가자 엔티티
package com.multi.backend5_1_multi_fc.match.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchParticipant {
    private Long participantId;
    private Long roomId;
    private Long userId;
    private String status;
    private LocalDateTime joinedAt;
}
