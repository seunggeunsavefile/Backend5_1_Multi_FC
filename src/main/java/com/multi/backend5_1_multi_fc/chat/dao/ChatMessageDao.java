package com.multi.backend5_1_multi_fc.chat.dao;

import com.multi.backend5_1_multi_fc.chat.dto.ChatMessageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageDao {
    List<ChatMessageDto> findMessagesByRoomId(Long roomId);
    void insertMessage(ChatMessageDto message);
    int countUnreadMessages(@Param("roomId") Long roomId, @Param("userId") Long userId);
    Long getLatestMessageId(@Param("roomId") Long roomId);
    void deleteMessage(Long messageId); // 메세지 삭제
    void updateMessage(ChatMessageDto message); // 메세지 수정
}