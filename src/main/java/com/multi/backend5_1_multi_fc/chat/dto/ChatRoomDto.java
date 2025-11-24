package com.multi.backend5_1_multi_fc.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    private Long roomId;
    private String roomType;
    private String roomName;
    private Long creatorId;
    private int memberCount;
    private int unreadCount;
    private Integer maxParticipants;
}
