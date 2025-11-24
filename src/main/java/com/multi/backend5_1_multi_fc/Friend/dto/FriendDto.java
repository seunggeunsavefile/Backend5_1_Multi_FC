package com.multi.backend5_1_multi_fc.friend.dto;

import lombok.Data;

import java.sql.Timestamp;

public class FriendDto {

    @Data
    public static class FriendListResponse {
        private Long friendId;   // friend 테이블 PK
        private Long userId;     // 상대 user_id
        private String username; // 상대 username
        private String nickname; // 상대 nickname
        private Timestamp createdAt;
    }

    @Data
    public static class FriendRequestResponse {
        private Long friendId;   // 요청 row PK
        private Long userId;     // 요청 보낸 사람 user_id
        private String username; // 요청 보낸 사람 username
        private String nickname; // 요청 보낸 사람 nickname
        private Timestamp createdAt;
    }

    @Data
    public static class FriendSearchResponse {
        private Long userId;
        private String username;
        private String nickname;
        private Boolean isFriend;  // 이미 친구/요청 상태면 true
    }

    @Data
    public static class SendFriendRequest {
        private Long targetUserId;
    }
}
