package com.multi.backend5_1_multi_fc.mypage.controller;

import com.multi.backend5_1_multi_fc.mypage.dto.MyPageDto;
import com.multi.backend5_1_multi_fc.mypage.exception.MyPageException;
import com.multi.backend5_1_multi_fc.mypage.service.MyPageService;
import com.multi.backend5_1_multi_fc.user.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final S3Service s3Service;

    // SecurityContext에서 username 가져오기
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new MyPageException("로그인이 필요한 요청입니다.");
        }
        return auth.getName();
    }

    // 내 정보 조회 (로그인한 유저 기준)
    @GetMapping("/me")
    public ResponseEntity<MyPageDto> getMyInfo(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String username =  getCurrentUsername();
        MyPageDto myInfo = myPageService.getMyInfo(username); // 여기서 DB 조회
        return ResponseEntity.ok(myInfo);
    }

    // 개인정보 수정 전 비밀번호 확인
    @PostMapping("/confirm-password")
    public ResponseEntity<?> confirmPassword(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody MyPageDto.PasswordVerifyRequest request
    ) {
        String username =  getCurrentUsername();
        try {
            myPageService.verifyPassword(username, request); // DB에서 현재 비번 검증
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "비밀번호가 확인되었습니다."
            ));
        } catch (MyPageException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // 개인정보 수정 (닉네임/이메일/포지션/지역 등)
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestPart("email") String email,
            @RequestPart("nickname") String nickname,
            @RequestPart("position") String position,
            @RequestPart("skillLevel") String level,
            @RequestPart("location") String address,
            @RequestPart(value = "profilePic", required = false) MultipartFile profilePic
    ) {
        String username =  getCurrentUsername();

        // 기존 내 정보 가져와서 gender / 기존 이미지 유지
        MyPageDto myInfo = myPageService.getMyInfo(username);

        // 서비스에서 쓰는 DTO로 다시 세팅
        MyPageDto.UpdateProfileRequest request = new MyPageDto.UpdateProfileRequest();
        request.setNickname(nickname);
        request.setPosition(position);
        request.setLevel(level);
        request.setAddress(address);
        request.setGender(myInfo.getGender());

        try {
            // 새 파일 있으면 S3 업로드
            if (profilePic != null && !profilePic.isEmpty()) {
                String imageUrl = s3Service.uploadFile(profilePic);
                request.setProfileImage(imageUrl);
            } else {
                // 파일 안 보냈으면 기존 이미지 URL 유지
                request.setProfileImage(myInfo.getProfileImage());
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "프로필 이미지 업로드 중 오류가 발생했습니다."));
        }

        myPageService.updateProfile(username, request);

        return ResponseEntity.ok(Map.of("message", "프로필이 성공적으로 수정되었습니다."));
    }

    // 비밀번호 변경
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody MyPageDto.UpdatePasswordRequest request
    ) {
        String username =  getCurrentUsername();
        myPageService.updatePassword(username, request); // DB 비밀번호 변경
        return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
    }

    // 마이페이지 예외 처리
    @ExceptionHandler(MyPageException.class)
    public ResponseEntity<String> handleMyPageException(MyPageException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}