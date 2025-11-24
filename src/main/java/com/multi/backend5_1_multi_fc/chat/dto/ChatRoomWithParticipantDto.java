package com.multi.backend5_1_multi_fc.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomWithParticipantDto {
    //채팅방 정보 + 추가 정보를 담는 응답용 Dto
    private Long roomId;
    private String roomType;
    private String roomName;
    private int memberCount;
    private int unreadCount;
}
