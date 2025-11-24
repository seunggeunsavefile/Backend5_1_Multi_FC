package com.multi.backend5_1_multi_fc.friend.repository;

import com.multi.backend5_1_multi_fc.friend.dao.FriendDao;
import com.multi.backend5_1_multi_fc.friend.dto.FriendDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendRepo {
    private final FriendDao dao;

    public Long userIdOf(String username) { return dao.findUserIdByUsername(username); }

    public List<FriendDto.FriendListResponse> myFriends(Long meId, String keyword) { return dao.findMyFriends(meId, keyword); }

    public List<FriendDto.FriendRequestResponse> incoming(Long meId) { return dao.findIncomingRequests(meId); }

    public List<FriendDto.FriendSearchResponse> search(Long meId, String keyword) { return dao.searchUsers(meId, keyword); }

    public int deleteBoth(Long meId, Long targetId) { return dao.deleteFriendBothWays(meId, targetId); }

    public int sendRequest(Long requesterId, Long targetId) { return dao.insertFriendRequest(requesterId, targetId); }

    public int accept(Long meId, Long requesterId) { return dao.approveFriend(meId, requesterId); }

    public int reject(Long meId, Long requesterId) { return dao.rejectFriend(meId, requesterId); }

    public boolean existsAny(Long a, Long b) { return dao.existsRelationAny(a, b); }
}

