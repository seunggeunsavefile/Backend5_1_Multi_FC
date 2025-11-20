// 경기방 생성 요청 DTO
package com.multi.backend5_1_multi_fc.match.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchRoomCreateReq {
    private Long roomId;
    private Long stadiumId;
    private Long hostId;      // 생성자(Host)
    private String matchDate;
    private String matchTime;
    private Integer maxPlayers;
    private String level;
}
