package com.multi.backend5_1_multi_fc.match.service;

import com.multi.backend5_1_multi_fc.match.dto.MatchRoomDto;
import com.multi.backend5_1_multi_fc.match.mapper.MatchRoomMapper;
import com.multi.backend5_1_multi_fc.match.mapper.MatchParticipantMapper;
import com.multi.backend5_1_multi_fc.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchScheduler {

    private final MatchRoomMapper matchRoomMapper;
    private final MatchParticipantMapper matchParticipantMapper;
    private final NotificationService notificationService;

    /**
     * 매 1분마다 실행되어 종료 시각이 지난 경기를 CLOSED 처리하고 후기 알림을 전송합니다.
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void closeExpiredMatches() {
        LocalDateTime now = LocalDateTime.now();

        // 1. 경기 종료 시각이 지난 매치룸 상태 자동 업데이트 (CLOSED 처리)
        matchRoomMapper.updateStatusToClosedIfExpired(now);

        // 2. ✨ 후기 알림 대상 매치룸 조회 (CLOSED 되지 않았고 시간이 지난 방)
        List<Long> expiredRoomIds = matchRoomMapper.findExpiredRoomsForReviewNotification(now);

        if (!expiredRoomIds.isEmpty()) {
            log.info("후기 알림 대상 경기방 ID: {}", expiredRoomIds);
        }

        for (Long roomId : expiredRoomIds) {

            // ⭐ 2-0. MatchRoom 정보(stadiumId) 조회
            MatchRoomDto room = matchRoomMapper.findById(roomId);
            if (room == null) continue;

            // 2-1. 해당 방의 확정 참가자(호스트 포함) ID 목록 조회
            List<Long> confirmedUserIds = matchParticipantMapper.findConfirmedParticipantUserIds(roomId);

            // 2-2. 각 확정 참가자에게 후기 알림 전송. stadiumId를 referenceId로 사용
            notificationService.sendReviewNotificationForMatch(room.getStadiumId(), confirmedUserIds);

            // 2-3. 알림 전송 후, 상태를 'CLOSED'로 변경하여 중복 알림 및 중복 처리를 방지
            matchRoomMapper.updateStatus(roomId, "CLOSED");
            log.info("경기방 ID {} 상태를 CLOSED로 변경 완료 및 후기 알림 전송 완료", roomId);
        }
    }
}