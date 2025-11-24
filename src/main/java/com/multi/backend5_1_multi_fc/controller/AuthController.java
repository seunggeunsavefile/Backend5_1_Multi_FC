package com.multi.backend5_1_multi_fc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.multi.backend5_1_multi_fc.user.dto.UserDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;


@Controller
public class AuthController {


    // 1. 메인 및 인증 페이지 (VIEW)
    @GetMapping("/")
    public String home() { return "main"; }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() { return "forgot-password"; }

    @GetMapping("/register")
    public String registerPage(HttpSession session, Model model) {
        // 1. 세션에서 "socialInfo"를 가져옵니다.
        UserDto socialInfo = (UserDto) session.getAttribute("socialInfo");

        if (socialInfo != null) {
            // 2. 모델에 socialInfo를 추가합니다.
            model.addAttribute("socialInfo", socialInfo);
            // 3. (중요) 세션에서 정보를 제거합니다 (새로고침 시 중복 방지)
            session.removeAttribute("socialInfo");
        }

        return "register";
    }

    @PostMapping("/find-id")
    public ResponseEntity<?> handleFindId(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(Map.of("message", "아이디 찾기 요청 접수됨 (구현 필요)"));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> handlePasswordResetRequest(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(Map.of("message", "비밀번호 재설정 이메일 발송됨 (구현 필요)"));
    }


    // 3. 프로필 수정 API 및 페이지 (VIEW/POST)
    @GetMapping("/profile/edit")
    public String profileEditPage() {
        return "profile-edit"; // templates/profile-edit.html
    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> handleProfileUpdate(
            @RequestParam String nickname,
            @RequestParam String position,
            @RequestParam String location,
            @RequestParam(required = false) MultipartFile profilePic
    ) {
        return ResponseEntity.ok().body(Map.of("message", "프로필이 성공적으로 업데이트되었습니다."));
    }

    @PostMapping("/profile/change-password")
    public ResponseEntity<?> handleChangePassword(@RequestBody Map<String, String> passwordRequest) {
        return ResponseEntity.ok().body(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
    }

    // 4. 구장, 일정, 마이페이지, 알림 (VIEW)
    @GetMapping("/friends")
    public String friendsPage() {
        return "friends"; // templates/friends.html
    }

    // 추가: 팀 생성 및 관리 페이지
    @GetMapping("/team/create")
    public String teamCreatePage() {
        return "team-create"; // templates/team-create.html
    }

    @GetMapping("/team/manage")
    public String teamManagePage(@RequestParam("id") Long id, Model model) {
        model.addAttribute("teamId", id);
        return "team-manage"; // templates/team-manage.html
    }


    @GetMapping("/fields")
    public String fieldsPage() {
        return "fields"; // 구장 검색 (fields.html)
    }

    // 구장 상세 페이지 매핑 (ID 기반)
    @GetMapping("/stadium/detail")
    public String stadiumDetailPage(@RequestParam("id") Long id, Model model) {
        model.addAttribute("stadiumId", id);
        return "stadium-detail"; // templates/stadium-detail.html
    }

    @GetMapping("/mypage")
    public String myPage() {
        return "mypage"; // 마이페이지 (mypage.html)
    }

    @GetMapping("/notifications")
    public String notificationsPage() {
        return "notifications"; // 알림 목록 (notifications.html)
    }

    @GetMapping("/schedule")
    public String schedulePage() {
        return "schedule"; // 일정 목록 (schedule.html)
    }

    @GetMapping("/schedule/add")
    public String addSchedulePage() {
        return "schedule-add"; // 새 일정 추가 (schedule-add.html)
    }

    @GetMapping("/schedule/detail/{id}")
    public String scheduleDetailPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("matchId", id);
        return "schedule-detail"; // 경기 일정 상세 (schedule-detail.html)
    }

    @GetMapping("/schedule/private/detail")
    public String privateScheduleDetailPage(@RequestParam("id") Long id, Model model) {
        model.addAttribute("scheduleId", id);
        return "schedule-private-detail";
    }

    @GetMapping("/reviews/write")
    public String writeReviewPage() {
        return "write-review"; // 후기 작성 (write-review.html)
    }

    // 5. 커뮤니티 및 채팅 (VIEW)
    @GetMapping("/community")
    public String communityPage() {
        return "community"; // community.html
    }

    @GetMapping("/community/write")
    public String communityWritePage() {
        return "community-write"; // community-write.html
    }

    @GetMapping("/community/detail/{postId}")
    public String communityDetailPage(@PathVariable("postId") Long postId, Model model) {
        model.addAttribute("postId", postId);
        return "community-detail"; // community-detail.html
    }

    @GetMapping("/chat")
    public String chatPage() {
        return "chat"; // chat.html (실시간채팅)
    }
}