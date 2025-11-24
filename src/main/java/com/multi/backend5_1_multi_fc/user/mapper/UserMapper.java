package com.multi.backend5_1_multi_fc.user.mapper;

import com.multi.backend5_1_multi_fc.user.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 1. 회원가입
     * @param userDto - DTO 객체
     */
    void insertUser(UserDto userDto);

    /**
     * 2. 중복 체크
     */
    int countByUsername(String username);
    int countByEmail(String email);
    int countByNickname(String nickname);

    /**
     * 3. 로그인 및 사용자 조회
     * @param username - 아이디
     * @return UserDto
     */
    UserDto findUserByUsername(String username);

    /**
     * 4. 아이디 찾기
     * @param email - 이메일
     * @return String (username)
     */
    String findUsernameByEmail(String email);

    /**
     * 5. 비밀번호 찾기 (1) - 아이디/이메일 일치 확인
     * (MyBatis에서 2개 이상의 파라미터는 @Param 어노테이션이 필요합니다)
     * @param username - 아이디
     * @param email - 이메일
     * @return boolean
     */
    boolean checkUserByUsernameAndEmail(@Param("username") String username, @Param("email") String email);

    /**
     * 6. 비밀번호 찾기 (2) - 인증코드 저장
     * @param email - 이메일
     * @param code - 생성된 6자리 코드
     */
    void updateResetCode(@Param("email") String email, @Param("code") String code);

    /**
     * 7. 비밀번호 찾기 (3) - 인증코드 검증
     * @param email - 이메일
     * @param code - 사용자가 입력한 코드
     * @return boolean
     */
    boolean verifyResetCode(@Param("email") String email, @Param("code") String code);

    /**
     * 8. 비밀번호 찾기 (4) - 새 비밀번호로 변경
     * @param email - 이메일
     * @param newPassword - 암호화된 새 비밀번호
     */
    void updatePasswordByEmail(@Param("email") String email, @Param("newPassword") String newPassword);

    /**
     * 9. 이메일로 사용자 정보 조회 (소셜 로그인용)
     * @param email - 이메일
     * @return UserDto
     */
    UserDto findUserByEmail(String email);

    /**
     * 10. 사용자 Id로 사용자 정보 조회 (Chat 용)
     * @param userId - 이메일
     * @return UserDto
     */
    UserDto findByUserId(Long userId);

    /**
     * 11. 사용자 닉네임으로 사용자들 정보 조회 (Chat 용)
     * @param nickname - 이메일
     * @return UserDto
     */
    List<UserDto> findUsersByNickname(String nickname);
}