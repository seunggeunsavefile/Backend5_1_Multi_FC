package com.multi.backend5_1_multi_fc.friend.mapper;

import com.multi.backend5_1_multi_fc.friend.dto.FriendDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendMapper {

    Long findUserIdByUsername(@Param("username") String username);

    List<FriendDto.FriendListResponse> findMyFriends(@Param("meId") Long meId,
                                                     @Param("keyword") String keyword);

    List<FriendDto.FriendRequestResponse> findIncomingRequests(@Param("meId") Long meId);

    List<FriendDto.FriendSearchResponse> searchUsers(@Param("meId") Long meId,
                                                     @Param("keyword") String keyword);

    int deleteFriendBothWays(@Param("meId") Long meId, @Param("targetId") Long targetId);

    int insertFriendRequest(@Param("requesterId") Long requesterId, @Param("targetId") Long targetId);

    int approveFriend(@Param("meId") Long meId, @Param("requesterId") Long requesterId);

    int rejectFriend(@Param("meId") Long meId, @Param("requesterId") Long requesterId);

    int existsRelationAny(@Param("a") Long a, @Param("b") Long b);
}
