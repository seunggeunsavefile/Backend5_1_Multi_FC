package com.multi.backend5_1_multi_fc.notification.service;


import com.multi.backend5_1_multi_fc.notification.dao.NotificationDao;
import com.multi.backend5_1_multi_fc.notification.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationDao notificationDao;
    private final SimpMessagingTemplate simpMessagingTemplate;

    //알림 생성 + DB 저장 + 실시간 전송
    public void createAndSendNotification(Long userId, String content, String type, Long referenceId){
        //알림 생성
        NotificationDto notification = NotificationDto.builder()
                .userId(userId)
                .content(content)
                .type(type)
                .referenceId(referenceId) //알람이 발생한 원인이 되는 데이터의 ID (ex: user_id, match_id, chat_room_id 등 등)
                .isRead(false)
                .build();

        //데이터베이스 저장
        notificationDao.insert(notification);

        //WebSocket 실시간 전송
        //userId에 해당하는 사용자의 /user/{userId}/queue/notifications로 메세지 전송 <- setUserDestinationPrefix("/user") 설정으로 인해 /user 접두사가 자동으로 붙음
        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(userId),             // 대상 사용자 ID
                "/queue/notifications",             // 구독 경로
                notification                        // 전송할 데이터
        );
    }

    public void createOrUpdateChatNotification(Long userId, String roomName, Long roomId){
        NotificationDto existingNotification = notificationDao.findUnreadChatNotification(userId, roomId);

        if(existingNotification != null){
            int currentCount = extractMessageCount(existingNotification.getContent());
            int newCount = currentCount + 1;

            String updatedContent = roomName + "방에 새로운 메세지 " + newCount + "건";
            existingNotification.setContent(updatedContent);

            notificationDao.updateContent(existingNotification.getNotificationId(), updatedContent);

            simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(userId),
                    "/queue/notifications",
                    existingNotification
            );
        } else {
            NotificationDto notification = NotificationDto.builder()
                    .userId(userId)
                    .content(roomName + " 방에 새로운 메시지 1건")
                    .type("채팅")
                    .referenceId(roomId)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationDao.insert(notification);

            simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(userId),
                    "/queue/notifications",
                    notification
            );
        }
    }
    private int extractMessageCount(String content){
        try{
            String[] parts = content.split(" ");
            for(int i =0; i < parts.length; i++){
                if(parts[i].endsWith("건")){
                    return Integer.parseInt(parts[i].replace("건",""));
                }
            }
        } catch (Exception e){
            return 1;
        }
        return 1;
    }

    //사용자 알림 목록 조회
    public List<NotificationDto> getUserNotifications(Long userId){
        return notificationDao.findByUserId(userId);
    }

    //읽지 않은 알림 조회
    public List<NotificationDto> getUnreadNotifications(Long userId){
        return notificationDao.findUnreadByUserId(userId);
    }

    //읽지 않은 알림 개수
    public int getUnreadCount(Long userId){
        return notificationDao.countUnreadByUserId(userId);
    }

    //알림 읽음 처리
    public void markAsRead(Long notificationId){
        notificationDao.updateReadStatus(notificationId);
    }

    //전체 읽음 처리
    public void markAllAsRead(Long userId){
        notificationDao.updateAllReadByUserId(userId);
    }

    //알림 삭제
    public void deleteNotification(Long notificationId){
        notificationDao.delete(notificationId);
    }

    //게시글에 새 댓글이 달렸을 때 알림
    public void createOrUpdatePostCommentNotification(Long userId,
                                                      Long postId,
                                                      Long lastCheckedCommentId,
                                                      Long currentCommentId) {

        // 마지막 확인 기준으로 새 댓글 없으면 알림 안 보냄
        if (currentCommentId == null
                || (lastCheckedCommentId != null && currentCommentId <= lastCheckedCommentId)) {
            return;
        }

        // 기존 미읽음 댓글 알림 있는지 조회
        NotificationDto existing =
                notificationDao.findUnreadCommentNotification(userId, postId);

        if (existing != null) {
            // 기존 알림 내용에서 "댓글 N건" 부분 숫자 증가
            int count = extractCommentCount(existing.getContent());
            int newCount = count + 1;

            String updatedContent = "내 게시글에 새로운 댓글 " + newCount + "건";
            existing.setContent(updatedContent);

            notificationDao.updateContent(existing.getNotificationId(), updatedContent);

            // WebSocket으로 다시 전송
            simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(userId),
                    "/queue/notifications",
                    existing
            );
        } else {
            // 새 알림 생성
            NotificationDto notification = NotificationDto.builder()
                    .userId(userId)
                    .type("댓글")           // 프론트 notif.type === '댓글'
                    .referenceId(postId)    // 게시글 상세로 이동할 postId
                    .content("내 게시글에 새로운 댓글 1건")
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationDao.insert(notification);

            simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(userId),
                    "/queue/notifications",
                    notification
            );
        }
    }

    // 대댓글 알림: “내 댓글에 대댓글 1건” 정도로 단순하게
    public void createReplyNotification(Long userId, Long postId) {
        NotificationDto notification = NotificationDto.builder()
                .userId(userId)
                .type("대댓글")          // 프론트 notif.type === '대댓글'
                .referenceId(postId)
                .content("내 댓글에 새로운 대댓글이 달렸습니다.")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationDao.insert(notification);

        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/notifications",
                notification
        );
    }

    // "댓글 N건" 형태에서 N 뽑아내기
    private int extractCommentCount(String content) {
        try {
            // 예: "내 게시글에 새로운 댓글 3건"
            String[] parts = content.split(" ");
            for (String part : parts) {
                if (part.endsWith("건")) {
                    return Integer.parseInt(part.replace("건", ""));
                }
            }
        } catch (Exception e) {
            // 파싱 안 되면 1로 기본값
        }
        return 1;
    }

    // ⭐⭐⭐ [수정] 경기 후기 알림 일괄 전송 (중복 방지 로직 추가) ⭐⭐⭐
    @Transactional
    public void sendReviewNotificationForMatch(Long stadiumId, List<Long> userIds) {
        String content = "참여했던 경기장 후기를 작성해 주세요! (구장 ID: " + stadiumId + ")";
        String type = "후기";

        for (Long userId : userIds) {

            // 핵심 중복 방지: 이미 읽지 않은 동일한 후기 알림이 있는지 확인
            NotificationDto existing = notificationDao.findUnreadNotificationByTypeAndReference(userId, type, stadiumId);

            if (existing != null) {
                continue; // 이미 알림이 있으므로 생성하지 않음
            }

            // 알림 생성
            NotificationDto notification = NotificationDto.builder()
                    .userId(userId)
                    .content(content)
                    .type(type)
                    .referenceId(stadiumId)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            // DB 저장
            notificationDao.insert(notification);

            // WebSocket 실시간 전송
            simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(userId),
                    "/queue/notifications",
                    notification
            );
        }
    }
    // ⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐

    // 친구 요청 알림 처리 (수락/거절 시 호출)
    public void markFriendRequestNotificationHandled(Long receiverUserId, Long requesterUserId) {
        NotificationDto notif =
                notificationDao.findUnreadNotificationByTypeAndReference(
                        receiverUserId,
                        "친구신청",
                        requesterUserId
                );
        if (notif == null) {
            return;
        }
        notificationDao.updateReadStatus(notif.getNotificationId());
    }
}
