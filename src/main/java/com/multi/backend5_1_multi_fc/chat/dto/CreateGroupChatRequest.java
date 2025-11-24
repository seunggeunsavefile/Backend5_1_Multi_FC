package com.multi.backend5_1_multi_fc.chat.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


//그룹 채팅 Dto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupChatRequest {
    private String roomType;
    private String roomName;
    private List<Long> invitedUserIds;
    private Integer maxParticipants;
}
