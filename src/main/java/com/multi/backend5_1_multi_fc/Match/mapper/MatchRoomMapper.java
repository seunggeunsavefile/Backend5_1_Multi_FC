package com.multi.backend5_1_multi_fc.match.mapper;

import com.multi.backend5_1_multi_fc.match.dto.MatchRoomCreateReq;
import com.multi.backend5_1_multi_fc.match.dto.MatchRoomDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MatchRoomMapper {

    void insert(MatchRoomCreateReq req);

    MatchRoomDto findById(Long roomId); // ⭐ Room 정보 조회를 위해 사용됩니다.

    List<MatchRoomDto> findByStadium(Long stadiumId);

    List<MatchRoomDto> findByUserId(@Param("userId") Long userId);

    void updateStatus(@Param("roomId") Long roomId, @Param("status") String status);

    // ✨ 자동 마감 로직을 위한 메서드
    int updateStatusToClosedIfExpired(@Param("currentTime") LocalDateTime currentTime);

    // ✨ [추가된 메서드] 후기 알림 대상 매치룸 ID 리스트 조회
    List<Long> findExpiredRoomsForReviewNotification(@Param("currentTime") LocalDateTime currentTime);
}