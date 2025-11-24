//package com.multi.backend5_1_multi_fc.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        String sql =
//                "SELECT user_id, username, password, nickname, profile_image, address, level, position, login_fail_count, locked_until, gender, created_at, updated_at, email, reset_code, reset_code_expires FROM User WHERE username = ?";
//        return jdbcTemplate.query(sql, rs -> {
//            if (!rs.next()) {
//                throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
//            }
//
//            String dbUsername = rs.getString("username");
//            String dbPassword = rs.getString("password");
//            if (dbPassword != null) {
//                dbPassword = dbPassword.trim();
//            }
//            // 기존 User 객체 반환 로직
////            if (/* 조건: 기본 User 객체로 반환이 필요할 때 */) {
////                return User.withUsername(dbUsername)
////                        .password(dbPassword)
////                        .roles("USER")
////                        .build();
////            }
//            // CustomUserDetails 객체 반환: userId가 필요한 케이스
//            return new CustomUserDetails(
//                    rs.getLong("user_id"),
//                    dbUsername,
//                    dbPassword,
//                    rs.getString("nickname"),
//                    rs.getString("profile_image"),
//                    rs.getString("address"),
//                    rs.getString("level"),
//                    rs.getString("position"),
//                    rs.getInt("login_fail_count"),
//                    rs.getTimestamp("locked_until"),
//                    rs.getString("gender"),
//                    rs.getTimestamp("created_at"),
//                    rs.getTimestamp("updated_at"),
//                    rs.getString("email"),
//                    rs.getString("reset_code"),
//                    rs.getTimestamp("reset_code_expires")
//            );
//        }, username);
//
//    }
//}

package com.multi.backend5_1_multi_fc.security;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String sql =
                "SELECT user_id, username, password, nickname, profile_image, address, level, position, login_fail_count, locked_until, gender, created_at, updated_at, email, reset_code, reset_code_expires FROM User WHERE username = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
            }

            String dbUsername = rs.getString("username");
            String dbPassword = rs.getString("password");
            if (dbPassword != null) {
                dbPassword = dbPassword.trim();
            }
            // 기존 User 객체 반환 로직
//            if (/* 조건: 기본 User 객체로 반환이 필요할 때 */) {
//                return User.withUsername(dbUsername)
//                        .password(dbPassword)
//                        .roles("USER")
//                        .build();
//            }
            // CustomUserDetails 객체 반환: userId가 필요한 케이스
            return new CustomUserDetails(
                    rs.getLong("user_id"),
                    dbUsername,
                    dbPassword,
                    rs.getString("nickname"),
                    rs.getString("profile_image"),
                    rs.getString("address"),
                    rs.getString("level"),
                    rs.getString("position"),
                    rs.getInt("login_fail_count"),
                    rs.getTimestamp("locked_until"),
                    rs.getString("gender"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at"),
                    rs.getString("email"),
                    rs.getString("reset_code"),
                    rs.getTimestamp("reset_code_expires")
            );
        }, username);

    }
}