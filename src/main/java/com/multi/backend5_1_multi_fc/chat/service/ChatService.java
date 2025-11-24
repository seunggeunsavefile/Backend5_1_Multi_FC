package com.multi.backend5_1_multi_fc.chat.service;

import com.multi.backend5_1_multi_fc.chat.dao.ChatMessageDao;
import com.multi.backend5_1_multi_fc.chat.dao.ChatParticipantDao;
import com.multi.backend5_1_multi_fc.chat.dao.ChatRoomDao;
import com.multi.backend5_1_multi_fc.chat.dto.*;
import com.multi.backend5_1_multi_fc.notification.service.NotificationService;
import com.multi.backend5_1_multi_fc.user.dto.UserDto;
import com.multi.backend5_1_multi_fc.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomDao chatRoomDao;
    private final ChatMessageDao chatMessageDao;
    private  final ChatParticipantDao chatParticipantDao;
    private final UserMapper userMapper;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    // 사용자의 채팅방 목록 조회 (타입별) - 동적으로 방 생성된 상태
    public List<ChatRoomWithParticipantDto> getUserChatRoomsByType(Long userId, String roomType){
        List<ChatRoomDto> rooms = chatRoomDao.findChatRoomsByUserIdAndType(userId, roomType);

        return rooms.stream().map(room -> {
            ChatRoomWithParticipantDto result = new ChatRoomWithParticipantDto();
            result.setRoomId(room.getRoomId());
            result.setRoomType(room.getRoomType());
            result.setMemberCount(room.getMemberCount());

            //1대1 채팅방 시 상대방 닉네임으로 표시
            if("1대1".equals(room.getRoomType())){
                String dynamicRoomName = generateOneToOneRoomName(room.getRoomId(), userId);
                result.setRoomName(dynamicRoomName);
            } else {
                result.setRoomName(room.getRoomName());
            }

            return result;
        }).collect(Collectors.toList());
    }

    //1대1 채팅방 이름 동적 생성(상대방 닉네임)
    private String generateOneToOneRoomName(Long roomId, Long currentUserId){
        List<ChatParticipantDto> participants = chatParticipantDao.findParticipantsByRoomId(roomId);

        ChatParticipantDto opponent = participants.stream()
                .filter(p -> !p.getUserId().equals(currentUserId))
                .findFirst()
                .orElse(null);

        if(opponent != null){
            UserDto opponentUser = userMapper.findByUserId(opponent.getUserId());
            return opponentUser.getNickname() + "님과의 채팅";
        }

        return "1대1 채팅";
    }

    //채팅방 조회
    public ChatRoomDto findChatRoomById(Long roomId){
        return chatRoomDao.findChatRoomById(roomId);
    }


    //1대 1 채팅방 버튼
    public ChatRoomDto createOrGetOneToOneChatRoom(Long userId1, Long userId2){
        ChatRoomDto existingRoom = chatRoomDao.findOneToOneChatRoom(userId1, userId2);

        if(existingRoom != null){
            return existingRoom;
        }

        Long currentLastRoom = chatRoomDao.getLastRoomId();
        UserDto targetUser = userMapper.findByUserId(userId2);

        ChatRoomDto newRoom = ChatRoomDto.builder()
                .roomId(currentLastRoom + 1)
                .roomType("1대1")
                .roomName(targetUser.getNickname() + " 님과의 채팅")
                .creatorId(userId1)
                .memberCount(2)
                .maxParticipants(2)
                .build();

        chatRoomDao.insertChatRoom(newRoom);

        if(chatParticipantDao.findByUserIdAndRoomId(userId1, newRoom.getRoomId()) == null){
            chatParticipantDao.insertParticipant(ChatParticipantDto.builder()
                    .roomId(newRoom.getRoomId())
                    .userId(userId1)
                    .role("creator")
                    .build()
            );
        }

        if(chatParticipantDao.findByUserIdAndRoomId(userId2, newRoom.getRoomId()) == null){
            chatParticipantDao.insertParticipant(ChatParticipantDto.builder()
                    .roomId(newRoom.getRoomId())
                    .userId(userId2)
                    .role("member")
                    .build()
            );
        }
        return newRoom;
    }

    //채팅방 생성 (그룹 채팅용)
    public ChatRoomDto createGroupChatRoom(Long creatorId, CreateGroupChatRequest request){
        Long currentLastRoom = chatRoomDao.getLastRoomId();

        Integer maxParticipants = request.getMaxParticipants();
        if (maxParticipants == null || maxParticipants <= 0) {
            maxParticipants = 10;  // 기본값: 10명
        }
        ChatRoomDto newRoom = ChatRoomDto.builder()
                .roomId(currentLastRoom + 1)
                .roomType(request.getRoomType())
                .roomName(request.getRoomName())
                .creatorId(creatorId)
                .memberCount(request.getInvitedUserIds().size() + 1)
                .maxParticipants(maxParticipants)
                .build();

        chatRoomDao.insertChatRoom(newRoom);

        //방장 추가
        chatParticipantDao.insertParticipant(ChatParticipantDto.builder()
                .roomId(newRoom.getRoomId())
                .userId(creatorId)
                .role("creator")
                .build()
        );

        for(Long userId: request.getInvitedUserIds()){
            if(userId.equals(creatorId)){
                System.out.println("방장 제외" + userId);
                continue;
            }

            ChatParticipantDto existingParticipant = chatParticipantDao.findByUserIdAndRoomId(userId, newRoom.getRoomId());
            if(existingParticipant != null){
                System.out.println("이미 참가 중 " + userId);
                continue;
            }

            chatParticipantDao.insertParticipant(ChatParticipantDto.builder()
                    .roomId(newRoom.getRoomId())
                    .userId(userId)
                    .role("member")
                    .build()
            );

            notificationService.createAndSendNotification(
                    userId,
                    request.getRoomName() + "채팅방에 초대되었습니다.",
                    "채팅",
                    newRoom.getRoomId()
            );
        }
        return newRoom;
    }





    //채팅 메세지 저장 + MQ publish
    public void sendMessage(ChatMessageDto messageDto){
        //DB 메세지 저장
        chatMessageDao.insertMessage(messageDto);

        //RabbitMQ로 publish
        messagingTemplate.convertAndSend("/topic/chatroom/" + messageDto.getRoomId(), messageDto);

        ChatRoomDto chatRoom = chatRoomDao.findChatRoomById(messageDto.getRoomId());

        List<ChatParticipantDto> participants = chatParticipantDao.findParticipantsByRoomId(messageDto.getRoomId());

        for(ChatParticipantDto participant : participants){
            if(!participant.getUserId().equals(messageDto.getSenderId())){
                String notificationRoomName;
                if("1대1".equals(chatRoom.getRoomType())){
                    notificationRoomName = generateOneToOneRoomName(messageDto.getRoomId(), participant.getUserId());
                } else {
                    notificationRoomName = chatRoom.getRoomName();
                }

                notificationService.createOrUpdateChatNotification(
                        participant.getUserId(),
                        notificationRoomName,
                        messageDto.getRoomId()
                );
            }
        }
    }

    //채팅방 메세지 목록 조회
    public List<ChatMessageDto> getMessagesByRoomId(Long userId, Long roomId) throws AccessDeniedException {
        ChatParticipantDto participant = chatParticipantDao.findByUserIdAndRoomId(userId, roomId);
        if(participant == null){
            throw new AccessDeniedException("채팅방에 접근 권한이 없습니다.");
        }

        return chatMessageDao.findMessagesByRoomId(roomId);
    }

    //채팅방 참가자 목록 조회
    public List<ChatParticipantDto> getParticipantsByRoomId(Long roomId){
        return chatParticipantDao.findParticipantsByRoomId(roomId);
    }

    //채팅방 참가자 추가
    public void addParticipant(ChatParticipantDto participant){
        ChatParticipantDto existingParticipant = chatParticipantDao.findByUserIdAndRoomId(participant.getUserId(), participant.getRoomId());
        if(existingParticipant != null){
            throw new IllegalStateException("이미 해당 채팅방에 참여 중입니다.");
        }

        //초대받은 사용자에게 알림
        ChatRoomDto chatRoom = chatRoomDao.findChatRoomById(participant.getRoomId());

        if (chatRoom.getMaxParticipants() != null
                && chatRoom.getMemberCount() >= chatRoom.getMaxParticipants()) {
            throw new IllegalStateException("채팅방 인원이 가득 찼습니다. (최대 "
                    + chatRoom.getMaxParticipants() + "명)");
        }

        chatParticipantDao.insertParticipant(participant);

        notificationService.createAndSendNotification(
                participant.getUserId(),
                chatRoom.getRoomName() + "채팅방에 초대되었습니다.",
                "채팅",
                participant.getRoomId()
        );
    }

    //unread 반환 둘 중 하나
    public List<ChatRoomDto> getChatRoomsWithUnreadCount(Long userId){
        List<ChatRoomDto> chatRooms = chatRoomDao.findChatRoomsByUserId(userId);
        for(ChatRoomDto room : chatRooms){
            int unread = chatMessageDao.countUnreadMessages(room.getRoomId(), userId);
            room.setUnreadCount(unread);
        }
        return chatRooms;
    }

    public int getUnreadMessageCount(Long roomId, Long userId) {
        return chatMessageDao.countUnreadMessages(roomId, userId);
    }

    public void markRoomAsRead(Long roomId, Long userId){
        Long latestMessageId = chatMessageDao.getLatestMessageId(roomId);
        System.out.println("Before latestMessageId = " + latestMessageId);
        chatParticipantDao.updateLastReadMessageId(roomId, userId, latestMessageId);
        System.out.println("After latestMessageId = " + latestMessageId);
    }


    //채팅방 강퇴기능 ( 실현 시킬지는 미지수 )
    public void removeParticipant(Long roomId, Long chatPartId){
        chatParticipantDao.deleteParticipant(roomId,chatPartId);
    }

    //채팅방 자발적으로 나가기
    public void leaveChatRoom(Long userId, Long roomId){
        ChatParticipantDto participant = chatParticipantDao.findByUserIdAndRoomId(userId,roomId);
        if(participant == null){
            throw new IllegalStateException("해당 채팅방의 참가자가 아닙니다.");
        }
        chatParticipantDao.deleteByUserIdAndRoomId(userId,roomId);

        ChatRoomDto chatRoom = chatRoomDao.findChatRoomById(roomId);
        int newMemberCount = chatRoom.getMemberCount() - 1;
        chatRoomDao.updateMemberCount(roomId, newMemberCount);

        if(newMemberCount == 0){
            chatRoomDao.deleteChatRoom(roomId);
        }
    }
}
