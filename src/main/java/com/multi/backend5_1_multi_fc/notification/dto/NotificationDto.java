package com.multi.backend5_1_multi_fc.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long notificationId;
    private Long userId;
    private String type;
    private Long referenceId;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
}