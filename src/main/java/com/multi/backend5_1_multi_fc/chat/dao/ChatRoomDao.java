package com.multi.backend5_1_multi_fc.chat.dao;

import com.multi.backend5_1_multi_fc.chat.dto.ChatRoomDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatRoomDao {
    List<ChatRoomDto> findChatRoomsByUserIdAndType(@Param("userId") Long userId,@Param("roomType") String roomType);
    List<ChatRoomDto> findChatRoomsByUserId(@Param("userId") Long userId);
    ChatRoomDto findOneToOneChatRoom(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    ChatRoomDto findChatRoomById(Long roomId);
    void insertChatRoom(ChatRoomDto chatRoom);
    void updateMemberCount(Long roomId, int memberCount);
    void deleteChatRoom(Long roomId);
    Long getLastRoomId();
}
