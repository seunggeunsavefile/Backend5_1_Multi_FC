package com.multi.backend5_1_multi_fc.user.service;

import com.multi.backend5_1_multi_fc.user.dao.UserDao;
import com.multi.backend5_1_multi_fc.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.Collections;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = userDao.findUserByUsername(username);
        if (userDto == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        // Spring Security의 User 객체로 변환하여 반환 (권한은 일단 비워둠)
        return new User(userDto.getUsername(), userDto.getPassword(), Collections.emptyList());
    }
    public List<UserDto> searchUsersByNickname(String nickname) {
        return userDao.findUsersByNickname(nickname);
    }
    public UserDto findUserById(Long userId) {
        return userDao.findByUserId(userId);
    }
    public UserDto getUserByUsername(String username) {
        log.info("🔍 getUserByUsername 호출: username={}", username);
        UserDto user = userDao.findUserByUsername(username);

        if (user == null) {
            log.error("❌ 사용자를 찾을 수 없음: {}", username);
            throw new RuntimeException("User not found: " + username);
        }

        log.info("✅ 사용자 조회 성공: userId={}, nickname={}", user.getUserId(), user.getNickname());
        return user;
    }

    @Transactional
    public void signup(UserDto userDto, MultipartFile profileImage) throws IOException {
        // 1. 중복 체크 (signup 자체 로직)
        if (userDao.countByUsername(userDto.getUsername()) > 0) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }
        if (userDao.countByEmail(userDto.getEmail()) > 0) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
        if (userDao.countByNickname(userDto.getNickname()) > 0) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }

        // 2. 프로필 이미지 처리 (성별에 따라 다른 기본 이미지)
        String imageUrl;
        if (profileImage != null && !profileImage.isEmpty()) {
            // 사용자가 직접 업로드한 경우
            imageUrl = s3Service.uploadFile(profileImage);
        } else {
            // 기본 이미지 사용 (성별에 따라)
            if ("남성".equals(userDto.getGender())) {
                imageUrl = "https://multifc-profile-images.s3.ap-northeast-2.amazonaws.com/profile/tiger_profile_square.png";
            } else {
                imageUrl = "https://multifc-profile-images.s3.ap-northeast-2.amazonaws.com/profile/rabbit_profile_square.png";
            }
        }

        userDto.setProfileImage(imageUrl );


        // 3. 비밀번호 암호화 (기존 코드 유지)
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // 4. DB 저장 (기존 코드 유지)
        userDao.insertUser(userDto);
    }


    // 로그인 (비밀번호 비교)
    public UserDto login(String username, String rawPassword) {
        // 1. 아이디로 DB에서 유저 정보 조회 (암호화된 비번 포함)
        UserDto user = userDao.findUserByUsername(username);

        // 2. 유저가 존재하고, 입력된 비밀번호(raw)와 DB의 암호화된 비밀번호(encoded)가 일치하는지 확인
        if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
            // (TODO: 로그인 성공 시 login_fail_count 0으로 리셋)
            return user; // 로그인 성공
        }

        // (TODO: 로그인 실패 시 login_fail_count 1 증가)
        return null; // 로그인 실패
    }

    // --- 실시간 중복 확인 API용 메서드 ---

    // 아이디 중복 검사
    public boolean isUsernameTaken(String username) {
        return userDao.countByUsername(username) > 0;
    }

    // 이메일 중복 검사
    public boolean isEmailTaken(String email) {
        return userDao.countByEmail(email) > 0;
    }

    // 닉네임 중복 검사
    public boolean isNicknameTaken(String nickname) {
        return userDao.countByNickname(nickname) > 0;
    }
    // 이메일로 마스킹된 아이디 반환
    public String findMyId(String email) {
        String username = userDao.findUsernameByEmail(email);

        if (username == null) {
            throw new IllegalStateException("일치하는 이메일 정보가 없습니다.");
        }

        // 아이디 마스킹 (앞 3자리만 보이고 나머지는 *)
        if (username.length() <= 3) {
            return username.substring(0, username.length() - 1) + "*"; // (3자리 미만 처리)
        }
        return username.substring(0, 3) + "*".repeat(username.length() - 3);
    }

    // 인증코드 요청
    @Transactional
    public void requestPasswordReset(String username, String email) {
        // 1. 아이디와 이메일이 모두 일치하는 사용자가 있는지 확인
        if (!userDao.checkUserByUsernameAndEmail(username, email)) {
            System.out.println("비밀번호 찾기: 일치 정보 없음 - 인증코드 발송 안 함");
            throw new IllegalStateException("입력하신 아이디와 이메일이 일치하지 않습니다.");
        }

        // 2. 6자리 랜덤 인증코드 생성
        String code = generateRandomCode();

        // 3. DB에 코드와 만료시간(5분) 저장 (이메일 기준)
        userDao.updateResetCode(email, code);

        //  이메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email); // [받는 사람] (사용자가 입력한 이메일)
        message.setFrom(fromEmail); // [보내는 사람] (properties의 multifc@gmail.com)
        message.setSubject("[Multi FC] 비밀번호 재설정 인증코드입니다.");
        message.setText("인증코드는 [ " + code + " ] 입니다. 5분 이내에 입력해주세요.");

        try {
            javaMailSender.send(message);
            System.out.println("✅ 이메일 발송 성공!");
        } catch (Exception e) {
            System.err.println("❌ 이메일 발송 실패: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("이메일 발송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }

    }

    // 인증코드 검증
    public void verifyPasswordResetCode(String email, String code) {
        if (!userDao.verifyResetCode(email, code)) {
            throw new IllegalStateException("인증코드가 올바르지 않거나 만료되었습니다.");
        }
    }

    // 비밀번호 재설정
    @Transactional
    public void confirmPasswordReset(String email, String code, String newPassword) {
        // 1. 재설정 직전에 한 번 더 검증 (보안 강화)
        if (!userDao.verifyResetCode(email, code)) {
            throw new IllegalStateException("인증코드가 올바르지 않거나 만료되었습니다.");
        }

        // 2. 새 비밀번호 암호화 후 저장
        String encodedPassword = passwordEncoder.encode(newPassword);
        userDao.updatePasswordByEmail(email, encodedPassword);
    }

    // 6자리 숫자 인증코드 생성 헬퍼
    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }
}