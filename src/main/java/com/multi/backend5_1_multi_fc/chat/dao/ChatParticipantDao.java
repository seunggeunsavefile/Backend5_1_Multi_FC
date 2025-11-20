package com.multi.backend5_1_multi_fc.chat.dao;

import com.multi.backend5_1_multi_fc.chat.dto.ChatParticipantDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatParticipantDao {
    List<ChatParticipantDto> findParticipantsByRoomId(Long roomId);
    ChatParticipantDto findByUserIdAndRoomId(@Param("userId") Long userId, @Param("roomId") Long roomId); //참가자 찾기
    void insertParticipant(ChatParticipantDto participant);
    void deleteParticipant(Long roomId, Long userId); //참가자 강퇴
    void deleteByUserIdAndRoomId(@Param("userId") Long userId, @Param("roomId") Long roomId); // userId와 roomId로 참가자 삭제
    void updateLastReadMessageId(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("lastReadMessageId") Long lastReadMessageId);
}