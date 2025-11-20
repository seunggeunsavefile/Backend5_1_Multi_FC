// 경기방 응답 DTO
package com.multi.backend5_1_multi_fc.match.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchRoomDto {
    private Long roomId;
    private Long stadiumId;
    private Long hostId;      // 생성자(Host)
    private String matchDate;
    private String matchTime;
    private Integer maxPlayers;
    private String level;
    private String status;
}
