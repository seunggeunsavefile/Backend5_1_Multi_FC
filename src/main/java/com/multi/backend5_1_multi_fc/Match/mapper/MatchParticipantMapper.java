package com.multi.backend5_1_multi_fc.match.mapper;

import com.multi.backend5_1_multi_fc.match.dto.ParticipantDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MatchParticipantMapper {

    void insert(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("position") String position);

    int existsByRoomAndUser(@Param("roomId") Long roomId, @Param("userId") Long userId);

    List<Long> findUserIdsByRoom(@Param("roomId") Long roomId);

    void delete(@Param("roomId") Long roomId, @Param("userId") Long userId);

    int countByRoom(@Param("roomId") Long roomId);

    // ⭐️ [추가] userId로 User 테이블의 position을 조회하는 메서드
    String findUserDefaultPosition(@Param("userId") Long userId);

    ParticipantDto findHostInfo(@Param("roomId") Long roomId);
    List<ParticipantDto> findParticipantsInfo(@Param("roomId") Long roomId);

    void updateStatus(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("status") String status);

    // ✨ [추가된 메서드] 특정 방의 확정 참가자(호스트 포함) ID 목록 조회
    List<Long> findConfirmedParticipantUserIds(@Param("roomId") Long roomId);
}