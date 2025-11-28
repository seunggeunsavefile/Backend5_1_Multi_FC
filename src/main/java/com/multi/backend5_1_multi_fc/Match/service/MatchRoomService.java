package com.multi.backend5_1_multi_fc.match.service;

import com.multi.backend5_1_multi_fc.match.dto.MatchRoomCreateReq;
import com.multi.backend5_1_multi_fc.match.dto.MatchRoomDto;
import com.multi.backend5_1_multi_fc.match.mapper.MatchRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.LocalDate; // 💡 LocalDate import 추가
import java.time.LocalDateTime; // 💡 LocalDateTime import 추가
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchRoomService {

    private final MatchRoomMapper matchRoomMapper;
    private final MatchEventPublisher eventPublisher;

    @Transactional
    public MatchRoomDto create(MatchRoomCreateReq req) {

        // ⭐⭐⭐ [수정된 부분: LocalTime -> LocalDateTime을 사용한 자정 초과 처리] ⭐⭐⭐
        if (req.getEndTime() == null || req.getEndTime().trim().isEmpty()) {
            try {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // DTO의 matchDate 형식 가정

                // 1. 경기 날짜와 시작 시간을 합쳐 정확한 시점 생성
                LocalDate matchDate = LocalDate.parse(req.getMatchDate(), dateFormatter);
                LocalTime matchTime = LocalTime.parse(req.getMatchTime(), timeFormatter);
                LocalDateTime startDateTime = LocalDateTime.of(matchDate, matchTime);

                // 2. 기본 2시간을 더한 종료 시점 계산
                LocalDateTime endDateTime = startDateTime.plusHours(2);

                // 3. 계산된 종료 시점에서 '시간'만 추출하여 DTO의 endTime에 설정
                //    (endTime은 TIME 타입 컬럼이므로)
                req.setEndTime(endDateTime.toLocalTime().format(timeFormatter));

                // 4. 만약 종료 시점이 다음 날로 넘어갔다면 (예: 23:00 -> 01:00),
                //    DB의 match_date가 실제 매치 종료 시점을 잘못 나타낼 수 있으므로
                //    호출하는 쪽의 로직(프론트/매치 개설 로직)에서 matchDate와 endTime을 모두 LocalTime으로만 관리한다면
                //    이 로직은 DTO의 endTime만 수정합니다.

            } catch (Exception e) {
                // matchTime 또는 matchDate 파싱 오류 발생 시 예외 처리
                throw new IllegalArgumentException("경기 시간/날짜 처리에 실패했습니다. DTO 형식을 확인하세요. 오류: " + e.getMessage(), e);
            }
        }
        // ⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐

        matchRoomMapper.insert(req);
        MatchRoomDto newRoom = matchRoomMapper.findById(req.getRoomId());
        eventPublisher.publishNewMatchForStadium(newRoom.getStadiumId(), newRoom);
        return newRoom;
    }

    public MatchRoomDto findById(Long roomId) {
        return matchRoomMapper.findById(roomId);
    }

    public List<MatchRoomDto> findByStadium(Long stadiumId) {
        return matchRoomMapper.findByStadium(stadiumId);
    }

    // ⭐️ [추가] 내가 참가/생성한 경기 목록 조회
    public List<MatchRoomDto> findByUserId(Long userId) {
        return matchRoomMapper.findByUserId(userId);
    }

    @Transactional
    public void closeMatch(Long roomId) {
        matchRoomMapper.updateStatus(roomId, "CLOSED");
    }
}