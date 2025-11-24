package com.multi.backend5_1_multi_fc.mypage.dto;

import lombok.Data;

import java.sql.Timestamp;

//마이페이지 전용 DTO
@Data
public class MyPageDto {

    private Long userId;
    private String username;
    private String email;
    private String nickname;
    private String profileImage;
    private String address;
    private String level;
    private String position;
    private String gender;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    //비밀번호 확인 요청
    @Data
    public static class PasswordVerifyRequest {
        private String currentPassword;
    }

    //개인정보 수정 요청
    @Data
    public static class UpdateProfileRequest {
        private String nickname;
        private String address;
        private String level;
        private String position;
        private String gender;
        private String profileImage; // URL
    }

    //비밀번호 변경 요청

    @Data
    public static class UpdatePasswordRequest {
        private String currentPassword;
        private String newPassword;
    }
}
