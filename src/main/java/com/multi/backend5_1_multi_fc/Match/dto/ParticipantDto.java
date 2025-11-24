package com.multi.backend5_1_multi_fc.match.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDto {
    private Long userId;
    private String nickname;
    private String role; // Host, Player
    private String status; // 확정, 대기 (for Player)
    private String position; // FW, MF, DF, GK (새로 추가된 필드)
}