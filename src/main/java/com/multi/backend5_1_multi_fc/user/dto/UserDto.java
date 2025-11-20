package com.multi.backend5_1_multi_fc.user.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

@Data
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;          // PK (user_id -> userId)
    private String username;
    private String password;
    private String nickname;
    private String profileImage;  // profile_image -> profileImage
    private String address;
    private String email;
    private String level;
    private String position;
    private String gender;

    private Integer loginFailCount; // login_fail_count -> loginFailCount
    private Timestamp lockedUntil;    // locked_until -> lockedUntil
    private Timestamp createdAt;      // created_at -> createdAt
    private Timestamp updatedAt;      // updated_at -> updatedAt
    private String resetCode;         // reset_code -> resetCode
    private Timestamp resetCodeExpires; // reset_code_expires -> resetCodeExpires
}