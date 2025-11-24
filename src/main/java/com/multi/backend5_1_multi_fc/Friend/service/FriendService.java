package com.multi.backend5_1_multi_fc.friend.service;

import com.multi.backend5_1_multi_fc.friend.dto.FriendDto;
import com.multi.backend5_1_multi_fc.friend.exception.FriendException;
import com.multi.backend5_1_multi_fc.friend.repository.FriendRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepo repo;

    private Long meId(String username) {
        Long id = repo.userIdOf(username);
        if (id == null) throw new FriendException("사용자를 찾을 수 없습니다.");
        return id;
    }

    public List<FriendDto.FriendListResponse> myFriends(String username, String keyword) {
        return repo.myFriends(meId(username), (keyword == null ? null : keyword.trim()));
    }

    public List<FriendDto.FriendRequestResponse> incomingRequests(String username) {
        return repo.incoming(meId(username));
    }

    public List<FriendDto.FriendSearchResponse> searchUsersForFriend(String username, String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();
        return repo.search(meId(username), keyword.trim());
    }

    @Transactional
    public void deleteFriend(String username, Long targetUserId) {
        Long me = meId(username);
        if (me.equals(targetUserId)) throw new FriendException("자기 자신은 삭제할 수 없습니다.");
        int affected = repo.deleteBoth(me, targetUserId);
        if (affected == 0) throw new FriendException("삭제할 친구 관계가 없습니다.");
    }

    @Transactional
    public void sendFriendRequest(String username, Long targetUserId) {
        Long me = meId(username);
        if (me.equals(targetUserId)) throw new FriendException("자기 자신에게는 요청할 수 없습니다.");
        if (repo.existsAny(me, targetUserId)) throw new FriendException("이미 요청 중이거나 친구입니다.");
        int inserted = repo.sendRequest(me, targetUserId);
        if (inserted == 0) throw new FriendException("요청이 이미 존재합니다.");
    }

    @Transactional
    public void acceptRequest(String username, Long requesterUserId) {
        Long me = meId(username);
        int updated = repo.accept(me, requesterUserId);
        if (updated == 0) throw new FriendException("수락할 요청이 없습니다.");
    }

    @Transactional
    public void rejectRequest(String username, Long requesterUserId) {
        Long me = meId(username);
        int updated = repo.reject(me, requesterUserId);
        if (updated == 0) throw new FriendException("거절할 요청이 없습니다.");
    }
}

