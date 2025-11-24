////package com.multi.backend5_1_multi_fc.security;
////
////import lombok.Getter;
////import lombok.RequiredArgsConstructor;
////import org.springframework.security.core.GrantedAuthority;
////import org.springframework.security.core.authority.SimpleGrantedAuthority;
////import org.springframework.security.core.userdetails.UserDetails;
////
////import java.sql.Timestamp;
////import java.time.LocalDateTime;
////import java.util.Collection;
////import java.util.Collections;
////import java.util.List;
////
////@Getter
////@RequiredArgsConstructor
////public class CustomUserDetails implements UserDetails {
////
////    private final Long userId;                  // 사용자 ID
////    private final String username;              // 로그인한 ID
////    private final String password;              // 비밀번호
////    private final String email;                 // 이메일
////    private final String nickname;              // 닉네임
////    private final String profileImage;          // 프로필 이미지
////    private final String level;                 // 본인 수준
////    private final String position;              // 포지션
////    private final String gender;                // 성별
////    private final Integer loginFailCount;       // 로그인 실패 횟수
////    private final Timestamp lockedUntil;    // 로그인 잠금 기간
////    private final Long LastCheckedCommentId;          // 마지막으로 확인한 댓글 ID
////    private final String role;                  // 사용자 권한
////
////    public Long getUserId() {
////        return userId;
////    }
////
////    @Override
////    public Collection<? extends GrantedAuthority> getAuthorities() {
////        return Collections.singletonList(new SimpleGrantedAuthority("role"));
////    }
////
////    @Override
////    public String getPassword() {
////        return password;
////    }
////
////    @Override
////    public String getUsername() {
////        return username;
////    }
////
////    @Override
////    public boolean isAccountNonExpired() {
////        return true;
////    }
////
////    @Override
////    public boolean isAccountNonLocked() {
////        if(lockedUntil != null){
////            return LocalDateTime.now().isAfter(lockedUntil.toLocalDateTime());
////        }
////        return true;
////    }
////
////    @Override
////    public boolean isCredentialsNonExpired() {
////        return true;
////    }
////
////    @Override
////    public boolean isEnabled() {
////        return true;
////    }
////}
//
//package com.multi.backend5_1_multi_fc.security;
//
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.sql.Time;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//
//@Getter
//@RequiredArgsConstructor
//public class CustomUserDetails implements UserDetails {
//
//    private final Long userId;                  // 사용자 ID
//    private final String username;              // 로그인한 ID
//    private final String password;              // 비밀번호
//    private final String nickname;              // 닉네임
//    private final String profileImage;          // 프로필 이미지
//    private final String address;
//    private final String level;                 // 본인 수준
//    private final String position;              // 포지션
//    private final Integer loginFailCount;       // 로그인 실패 횟수
//    private final Timestamp lockedUntil;    // 로그인 잠금 기간
//    private final String gender;                // 성별
//    private final Timestamp createdAt;
//    private final Timestamp updatedAt;
//    private final String email;                 // 이메일
//    private final String resetCode;
//    private final Timestamp resetCodeExpires;
//    // private final String role;                  // 사용자 권한
//
//    public Long getUserId() {
//        return userId;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Collections.singletonList(new SimpleGrantedAuthority("role"));
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public String getUsername() {
//        return username;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        if(lockedUntil != null){
//            return LocalDateTime.now().isAfter(lockedUntil.toLocalDateTime());
//        }
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}

package com.multi.backend5_1_multi_fc.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Long userId;                  // 사용자 ID
    private final String username;              // 로그인한 ID
    private final String password;              // 비밀번호
    private final String nickname;              // 닉네임
    private final String profileImage;          // 프로필 이미지
    private final String address;
    private final String level;                 // 본인 수준
    private final String position;              // 포지션
    private final Integer loginFailCount;       // 로그인 실패 횟수
    private final Timestamp lockedUntil;    // 로그인 잠금 기간
    private final String gender;                // 성별
    private final Timestamp createdAt;
    private final Timestamp updatedAt;
    private final String email;                 // 이메일
    private final String resetCode;
    private final Timestamp resetCodeExpires;
    // private final String role;                  // 사용자 권한

    public Long getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("role"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if(lockedUntil != null){
            return LocalDateTime.now().isAfter(lockedUntil.toLocalDateTime());
        }
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}