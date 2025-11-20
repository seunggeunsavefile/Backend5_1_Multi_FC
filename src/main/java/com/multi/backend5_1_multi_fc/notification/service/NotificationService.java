package com.multi.backend5_1_multi_fc.notification.service;


import com.multi.backend5_1_multi_fc.notification.dao.NotificationDao;
import com.multi.backend5_1_multi_fc.notification.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
}