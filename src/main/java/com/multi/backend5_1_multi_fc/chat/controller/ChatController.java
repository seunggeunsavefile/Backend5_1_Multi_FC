package com.multi.backend5_1_multi_fc.chat.controller;

import com.multi.backend5_1_multi_fc.chat.dto.*;
import com.multi.backend5_1_multi_fc.chat.service.ChatService;
import com.multi.backend5_1_multi_fc.notification.service.NotificationService;
import com.multi.backend5_1_multi_fc.security.CustomUserDetails;
import com.multi.backend5_1_multi_fc.user.dto.UserDto;
import com.multi.backend5_1_multi_fc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "getAuthenticatedUserId 인증 정보가 없습니다.");
        }
        Object principal = authentication.getPrincipal();
        System.out.println("principal = " + principal + ", type = " + principal.getClass().getName());
        if (principal instanceof com.multi.backend5_1_multi_fc.security.CustomUserDetails customUser) {
            System.out.println("customUser = " + customUser);
            return customUser.getUserId();
        }
        // anonymousUser일 때
        if (principal instanceof String str && str.equals("anonymousUser")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요: anonymousUser");
        }

        // 그 외 (알 수 없는 인증 타입)
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 객체: " + principal.getClass().getName());
    }

    //1. 1대 1 채팅방 생성
    @PostMapping("/chatroom/onetoone")
    public ChatRoomDto createOneToOneChatRoom(
            @RequestParam("targetUserId") Long targetUserId
    ) {
        Long userId = getAuthenticatedUserId();
        return chatService.createOrGetOneToOneChatRoom(userId, targetUserId);
    }

    //2. 그룹 채팅방 생성
    @PostMapping("/chatroom/group")
    public ChatRoomDto createChatRoom(
            @RequestBody CreateGroupChatRequest request) {
        Long userId = getAuthenticatedUserId();
        return chatService.createGroupChatRoom(userId, request);
    }

    //3. 사용자의 채팅방 목록 조회 (타입별) - 동적 이름 포함
    @GetMapping("/chatroom")
    public List<ChatRoomWithParticipantDto> getChatRooms(
            @RequestParam("type") String roomType) {
        Long userId = getAuthenticatedUserId();
        List<ChatRoomWithParticipantDto> rooms = chatService.getUserChatRoomsByType(userId, roomType);
        for (ChatRoomWithParticipantDto room : rooms) {
            int unreadCount = chatService.getUnreadMessageCount(room.getRoomId(), userId);
            room.setUnreadCount(unreadCount);
        }
        return rooms;
    }

    //4. 채팅방 단일 조회 (방의 정보를 응답받는 형태 - 방 제목, 멤버 )
    @GetMapping("/chatroom/{id}")
    public ChatRoomDto getChatRoom(@PathVariable("id") Long roomId) {
        return chatService.findChatRoomById(roomId);
    }

    //5. 채팅방 메세지 목록
    @GetMapping("/chatroom/{id}/messages")
    public List<ChatMessageDto> getChatMessages(@PathVariable("id") Long roomId) throws AccessDeniedException {
        Long userId = getAuthenticatedUserId();
        return chatService.getMessagesByRoomId(userId, roomId);
    }

    //6. 채팅방 참가자 목록
    @GetMapping("/chatroom/{id}/participants")
    public List<ChatParticipantDto> getChatParticipants(@PathVariable("id") Long roomId) {
        return chatService.getParticipantsByRoomId(roomId);
    }

    //7. 채팅방 참가자 추가
    @PostMapping("/chatroom/{id}/invite")
    public void inviteParticipants(@PathVariable("id") Long roomId, @RequestBody List<Long> invitedUserIds) {

        for (Long userId : invitedUserIds) {
            chatService.addParticipant(createParticipantDto(roomId, userId));
        }
    }

    private ChatParticipantDto createParticipantDto(Long roomId, Long userId) {
        return ChatParticipantDto.builder()
                .roomId(roomId)
                .userId(userId)
                .build();
    }

    //8. 채팅방 나가기
    @DeleteMapping("/chatroom/{id}/leave")
    public void leaveChatRoom(@PathVariable("id") Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        chatService.leaveChatRoom(userDetails.getUserId(), roomId);
    }

    //8. 채팅 전송 기능
    @MessageMapping("/chatroom/{roomId}/send")
    public void sendMessage(@DestinationVariable Long roomId, @Payload ChatMessageDto messageDto, Principal principal) {

        try {
            String username = principal.getName();
            UserDto user = userService.getUserByUsername(username);
            Long userId = user.getUserId();

            System.out.println("username = " + username + ", userId = " + userId);

            messageDto.setRoomId(roomId);
            messageDto.setSenderId(userId);
            messageDto.setSenderNickname(username);

            chatService.sendMessage(messageDto);
        } catch (Exception e) {
            log.error("❌ 메시지 전송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("메시지 전송 실패: " + e.getMessage());
        }
    }

    //9. 안 읽은 채팅 숫자 반환
    @GetMapping("/chatroom/{roomId}/unread-count")
    public int getUnreadBadge(@PathVariable Long roomId) {
        Long userId = getAuthenticatedUserId();
        return chatService.getUnreadMessageCount(roomId, userId);
    }

    @PostMapping("/chatroom/{roomId}/mark-read")
    public ResponseEntity<Void> markRoomAsRead(@PathVariable Long roomId) {
        Long userId = getAuthenticatedUserId();
        System.out.println("markRoomAsRead Start");
        chatService.markRoomAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }
}