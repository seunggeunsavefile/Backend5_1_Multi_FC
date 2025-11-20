package com.multi.backend5_1_multi_fc.user.controller;

import com.multi.backend5_1_multi_fc.security.CustomUserDetails;
import com.multi.backend5_1_multi_fc.user.dto.UserDto;
import com.multi.backend5_1_multi_fc.user.service.UserService;
import com.multi.backend5_1_multi_fc.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // [기존] 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @ModelAttribute UserDto userDto,
            @RequestParam(value = "profile_image_file", required = false) MultipartFile profileImageFile
    ) {
        System.out.println("👉 [요청 도착] DTO: " + userDto);
        System.out.println("👉 [요청 도착] 파일: " + (profileImageFile != null ? profileImageFile.getOriginalFilename() : "없음"));

        try {
            userService.signup(userDto, profileImageFile);
            return new ResponseEntity<>("회원가입 성공", HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("서버 에러 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- [로그인 기능 추가] ---
    // login.html의 스크립트가 호출하는 API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String rawPassword = payload.get("password");

        try {
            // 1. 서비스로 아이디/비번을 보내 인증 요청
            UserDto user = userService.login(username, rawPassword);

            if (user != null) {
                // 2. 로그인 성공
                user.setPassword(null); // (보안) 응답에서 비밀번호 제거
                user.setResetCode(null);
                user.setResetCodeExpires(null);

                String realToken = jwtUtil.generateToken(user.getUsername());

                // 3. 프론트엔드로 토큰과 사용자 정보 반환
                Map<String, Object> response = new HashMap<>();
                response.put("accessToken", realToken);
                response.put("user", user);

                return ResponseEntity.ok(response);

            } else {
                System.out.println("❌ 로그인 실패: 아이디 또는 비밀번호 불일치");
                return new ResponseEntity<>("아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("로그인 중 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- [기존] 중복 확인 API ---

    // 아이디 중복 확인 API
    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        return ResponseEntity.ok(userService.isUsernameTaken(username));
    }

    // 이메일 중복 확인 API
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.isEmailTaken(email));
    }

    // 닉네임 중복 확인 API
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(userService.isNicknameTaken(nickname));
    }


    @PostMapping("/find-id")
    public ResponseEntity<?> findId(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String maskedUsername = userService.findMyId(email);

            // 성공 시 (예: { "username": "fut***" })
            Map<String, String> response = new HashMap<>();
            response.put("username", maskedUsername);
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            // 실패 시 (예: "일치하는 이메일 정보가 없습니다.")
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("아이디 찾기 중 서버 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 비밀번호 찾기 인증번호 요청
    @PostMapping("/reset-password/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody Map<String, String> payload) {
        try {
            String username = payload.get("username");
            String email = payload.get("email");
            userService.requestPasswordReset(username, email);

            // 성공 시
            return ResponseEntity.ok("인증코드가 이메일로 발송되었습니다. 메일함을 확인해주세요.");

        } catch (IllegalStateException e) {
            // [추가] 일치하는 정보가 없을 때
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("인증코드 발송 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 인증번호 검증
    @PostMapping("/reset-password/verify")
    public ResponseEntity<String> verifyPasswordResetCode(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String code = payload.get("code");
            userService.verifyPasswordResetCode(email, code);

            return ResponseEntity.ok("인증 성공");
        } catch (IllegalStateException e) {
            // (예: "인증코드가 올바르지 않거나 만료되었습니다.")
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("인증 중 서버 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 새 비밀번호로 변경
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<String> confirmPasswordReset(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String code = payload.get("code");
            String newPassword = payload.get("newPassword");

            userService.confirmPasswordReset(email, code, newPassword);

            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalStateException e) {
            // (예: "인증코드가 올바르지 않거나 만료되었습니다.")
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("비밀번호 변경 중 서버 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        log.info("📡 /api/users/me 호출됨");

        // ✅ SecurityContext에서 Authentication 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("❌ 인증 정보가 없음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // ✅ Principal에서 username 추출
        String username = authentication.getName();
        log.info("✅ 인증된 사용자: {}", username);

        if (username == null || username.equals("anonymousUser")) {
            log.error("❌ 익명 사용자");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // ✅ username으로 사용자 정보 조회
            UserDto user = userService.getUserByUsername(username);

            if (user == null) {
                log.error("❌ 사용자를 찾을 수 없음: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 보안: 민감 정보 제거
            user.setPassword(null);
            user.setResetCode(null);
            user.setResetCodeExpires(null);

            log.info("✅ 사용자 정보 조회 성공: userId={}, nickname={}", user.getUserId(), user.getNickname());
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            log.error("❌ 사용자 정보 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 수정: /search 엔드포인트
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsersByNickname(
            @RequestParam("nickname") String nickname) {

        log.info("📡 /api/users/search 호출됨, nickname={}", nickname);

        // ✅ 인증 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("❌ 인증 정보가 없음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<UserDto> users = userService.searchUsersByNickname(nickname);

            // 보안: 비밀번호 제거
            users.forEach(user -> {
                user.setPassword(null);
                user.setResetCode(null);
                user.setResetCodeExpires(null);
            });

            log.info("✅ 검색 성공: {}명 조회됨", users.size());
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            log.error("❌ 사용자 검색 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 기존: /{userId} 엔드포인트 (인증 불필요하므로 그대로 유지)
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable("userId") Long userId) {
        log.info("📡 /api/users/{} 호출됨", userId);

        try {
            UserDto user = userService.findUserById(userId);

            // 보안: 민감 정보 제거
            user.setPassword(null);
            user.setResetCode(null);
            user.setResetCodeExpires(null);
            user.setEmail(null); // 이메일도 숨김

            log.info("✅ 프로필 조회 성공: nickname={}", user.getNickname());
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            log.error("❌ 프로필 조회 실패: userId={}, error={}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}