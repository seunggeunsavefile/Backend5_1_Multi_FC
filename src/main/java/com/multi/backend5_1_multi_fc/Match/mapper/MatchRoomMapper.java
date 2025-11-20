package com.multi.backend5_1_multi_fc.match.mapper;

import com.multi.backend5_1_multi_fc.match.dto.MatchRoomCreateReq;
import com.multi.backend5_1_multi_fc.match.dto.MatchRoomDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MatchRoomMapper {

    void insert(MatchRoomCreateReq req);

    MatchRoomDto findById(Long roomId);

    List<MatchRoomDto> findByStadium(Long stadiumId);

    // ✅ [추가됨] 경기 상태 변경 (마감 처리용)
    void updateStatus(@Param("roomId") Long roomId, @Param("status") String status);
}