package com.multi.backend5_1_multi_fc.user.service;

import com.multi.backend5_1_multi_fc.user.dto.UserDto;
import com.multi.backend5_1_multi_fc.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = userMapper.findUserByUsername(username);
        if (userDto == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }
        return new User(userDto.getUsername(), userDto.getPassword(), Collections.emptyList());
    }
    public List<UserDto> searchUsersByNickname(String nickname) {
        return userMapper.findUsersByNickname(nickname);
    }
    public UserDto findUserById(Long userId) {
        return userMapper.findByUserId(userId);
    }
    public UserDto getUserByUsername(String username) {
        UserDto user = userMapper.findUserByUsername(username);

        if (user == null) {
            log.error("❌ 사용자를 찾을 수 없음: {}", username);
            throw new RuntimeException("User not found: " + username);
        }
        return user;
    }

    @Transactional
    public void signup(UserDto userDto, MultipartFile profileImageFile) throws IOException {
        if (userMapper.countByUsername(userDto.getUsername()) > 0) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }
        if (userMapper.countByEmail(userDto.getEmail()) > 0) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
        if (userMapper.countByNickname(userDto.getNickname()) > 0) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }

        String imageUrl = null;

        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            imageUrl = s3Service.uploadFile(profileImageFile);
        }

        else if (userDto.getProfileImage() != null && !userDto.getProfileImage().isEmpty()) {
            imageUrl = userDto.getProfileImage();
        }

        else {
            if ("남성".equals(userDto.getGender())) {
                imageUrl = "https://multifc-profile-images.s3.ap-northeast-2.amazonaws.com/profile/tiger_profile_square.png";
            } else {
                imageUrl = "https://multifc-profile-images.s3.ap-northeast-2.amazonaws.com/profile/rabbit_profile_square.png";
            }
        }

        // 최종 결정된 URL 저장
        userDto.setProfileImage(imageUrl);


        // 3. 비밀번호 암호화
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // 4. DB 저장
        userMapper.insertUser(userDto);
    }

    // 로그인 (비밀번호 비교)
    public UserDto login(String username, String rawPassword) {
        UserDto user = userMapper.findUserByUsername(username);

        if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
            return user;
        }
        return null;
    }

    // --- 실시간 중복 확인 API용 메서드 ---

    public boolean isUsernameTaken(String username) {
        return userMapper.countByUsername(username) > 0;
    }

    public boolean isEmailTaken(String email) {
        return userMapper.countByEmail(email) > 0;
    }

    public boolean isNicknameTaken(String nickname) {
        return userMapper.countByNickname(nickname) > 0;
    }
    // 이메일로 마스킹된 아이디 반환
    public String findMyId(String email) {
        String username = userMapper.findUsernameByEmail(email);

        if (username == null) {
            throw new IllegalStateException("일치하는 이메일 정보가 없습니다.");
        }

        if (username.length() <= 3) {
            return username.substring(0, username.length() - 1) + "*";
        }
        return username.substring(0, 3) + "*".repeat(username.length() - 3);
    }
    // [추가] 아이디로 회원 정보 전체 조회 (API용)
    public UserDto getUserProfile(String username) {
        UserDto userDto = userMapper.findUserByUsername(username);

        if (userDto != null) {
            String nick = userDto.getNickname();
        }
        return userDto;
    }

    // 인증코드 요청
    @Transactional
    public void requestPasswordReset(String username, String email) {
        if (!userMapper.checkUserByUsernameAndEmail(username, email)) {
            System.out.println("비밀번호 찾기: 일치 정보 없음 - 인증코드 발송 안 함");
            throw new IllegalStateException("입력하신 아이디와 이메일이 일치하지 않습니다.");
        }

        String code = generateRandomCode();

        userMapper.updateResetCode(email, code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(fromEmail);
        message.setSubject("[Multi FC] 비밀번호 재설정 인증코드입니다.");
        message.setText("인증코드는 [ " + code + " ] 입니다. 5분 이내에 입력해주세요.");

        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            System.err.println("❌ 이메일 발송 실패: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("이메일 발송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    // 인증코드 검증
    public void verifyPasswordResetCode(String email, String code) {
        if (!userMapper.verifyResetCode(email, code)) {
            throw new IllegalStateException("인증코드가 올바르지 않거나 만료되었습니다.");
        }
    }

    // 비밀번호 재설정
    @Transactional
    public void confirmPasswordReset(String email, String code, String newPassword) {
        if (!userMapper.verifyResetCode(email, code)) {
            throw new IllegalStateException("인증코드가 올바르지 않거나 만료되었습니다.");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        userMapper.updatePasswordByEmail(email, encodedPassword);
    }

    // 6자리 숫자 인증코드 생성 헬퍼
    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }
}