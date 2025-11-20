package com.multi.backend5_1_multi_fc.match.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDto {
    private Long userId;
    private String nickname;
    private String position;
    private String role;     // "Host" or "Player"
    private String status;   // ✅ [추가됨] "대기", "확정" 등
}