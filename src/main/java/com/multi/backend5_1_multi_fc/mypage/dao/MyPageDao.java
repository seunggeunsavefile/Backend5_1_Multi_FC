package com.multi.backend5_1_multi_fc.mypage.dao;

import com.multi.backend5_1_multi_fc.mypage.dto.MyPageDto;
import com.multi.backend5_1_multi_fc.mypage.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MyPageDao {

    private final MyPageMapper myPageMapper;

    public MyPageDto findByUsername(String username) {
        return myPageMapper.findByUsername(username);
    }

    public MyPageDto findByUserId(Long userId) {
        return myPageMapper.findByUserId(userId);
    }

    public String findPasswordByUserId(Long userId) {
        return myPageMapper.findPasswordByUserId(userId);
    }

    public int updateProfileByUserId(Long userId, MyPageDto.UpdateProfileRequest request) {
        return myPageMapper.updateProfileByUserId(userId, request);
    }

    public int updatePasswordByUserId(Long userId, String encodedPassword) {
        return myPageMapper.updatePasswordByUserId(userId, encodedPassword);
    }
}

