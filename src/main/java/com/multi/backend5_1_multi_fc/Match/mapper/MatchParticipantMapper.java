package com.multi.backend5_1_multi_fc.match.mapper;

import com.multi.backend5_1_multi_fc.match.dto.ParticipantDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MatchParticipantMapper {

    void insert(@Param("roomId") Long roomId, @Param("userId") Long userId);

    int existsByRoomAndUser(@Param("roomId") Long roomId, @Param("userId") Long userId);

    List<Long> findUserIdsByRoom(@Param("roomId") Long roomId);

    void delete(@Param("roomId") Long roomId, @Param("userId") Long userId);

    int countByRoom(@Param("roomId") Long roomId);

    // 조회
    ParticipantDto findHostInfo(@Param("roomId") Long roomId);
    List<ParticipantDto> findParticipantsInfo(@Param("roomId") Long roomId);

    // ✅ [추가됨] 상태 변경 (수락 기능)
    void updateStatus(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("status") String status);
}