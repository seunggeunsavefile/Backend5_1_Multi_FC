package com.multi.backend5_1_multi_fc.notification.controller;

import com.multi.backend5_1_multi_fc.chat.service.ChatService;
import com.multi.backend5_1_multi_fc.notification.dto.NotificationDto;
import com.multi.backend5_1_multi_fc.notification.service.NotificationService;
import com.multi.backend5_1_multi_fc.security.CustomUserDetails;
import com.multi.backend5_1_multi_fc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final JdbcTemplate jdbcTemplate;

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다.");
        }
        Object principal = authentication.getPrincipal();
        System.out.println("principal = " + principal + ", type = " + principal.getClass().getName());
        if (principal instanceof com.multi.backend5_1_multi_fc.security.CustomUserDetails customUser) {
            return customUser.getUserId();
        }
        // anonymousUser일 때
        if (principal instanceof String str && str.equals("anonymousUser")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요: anonymousUser");
        }

        // 그 외 (알 수 없는 인증 타입)
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 객체: " + principal.getClass().getName());
    }

    //GET /api/notifications
    //알림 목록 조회
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(){
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    //GET /api/notifications/unread
    //읽지 않은 알림 조회
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications() {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    //GET /api/notifications/unread/count
    //읽지 않은 알림 개수
    @GetMapping("/unread/count")
    public ResponseEntity<Integer> getUnreadCount() {
        Long userId = getAuthenticatedUserId();

        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    //PUT /api/notifications/read-all
    //전체 읽음 처리
    @PutMapping("read-all")
    public ResponseEntity<Void> markAllAsRead() {
        Long userId = getAuthenticatedUserId();
        notificationService.markAllAsRead(userId);

        return ResponseEntity.ok().build();
    }

    //PUT /api/notifications/{id}/read
    //특정 알림 읽음 처리
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id){
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    //DELETE /api/notifications/{id}
    //알림 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id){
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
}