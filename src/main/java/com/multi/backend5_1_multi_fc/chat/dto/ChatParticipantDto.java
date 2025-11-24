package com.multi.backend5_1_multi_fc.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipantDto {
    //채팅방 참가 정보
    private Long chatPartId;
    private Long roomId;
    private Long userId;
    private String role;
    private String nickname;
    private String profileImg;
    private LocalDateTime joinedAt;
    private Long lastReadMessageId;
}
