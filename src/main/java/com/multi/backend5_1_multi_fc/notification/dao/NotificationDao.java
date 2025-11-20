package com.multi.backend5_1_multi_fc.notification.dao;


import com.multi.backend5_1_multi_fc.notification.dto.NotificationDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationDao {
    //알림 생성
    void insert(NotificationDto notification);

    //사용자별 알림 조회
    List<NotificationDto> findByUserId(Long userId);

    //읽지 않은 알림 조회
    List<NotificationDto> findUnreadByUserId(Long userId);

    NotificationDto findUnreadChatNotification(@Param("userId") Long userId, @Param("referenceId") Long referenceId);

    void updateContent(@Param("notificationId") Long notificationId, @Param("content") String content);

    //읽지 않은 알림 개수
    int countUnreadByUserId(Long userId);

    //알림 읽음 처리
    void updateReadStatus(Long notificationId);

    //전체 알림 읽음 처리
    void updateAllReadByUserId(Long userId);

    //알림 삭제
    void delete(long notificationId);
}