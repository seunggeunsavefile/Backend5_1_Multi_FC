// 경기방 엔티티
package com.multi.backend5_1_multi_fc.match.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MatchRoom {
    private Long roomId;
    private Long stadiumId;
    private Long hostId;
    private LocalDate matchDate;
    private LocalTime matchTime;
    private Integer maxPlayers;
    private String level;
    private String status;
}
