package com.multi.backend5_1_multi_fc.security;

import com.multi.backend5_1_multi_fc.user.dto.UserDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil; // ⭐️ [추가] 토큰 생성을 위해 주입

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        boolean isNewUser = oAuth2User.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PRE_REGISTER"));

        System.out.println("========== SSO 로그인 성공 핸들러 ==========");

        if (isNewUser) {
            // (B) 신규 회원 -> 회원가입 페이지로
            UserDto socialInfo = new UserDto();
            socialInfo.setEmail((String) attributes.get("email"));
            socialInfo.setNickname((String) attributes.get("name"));
            socialInfo.setProfileImage((String) attributes.get("picture"));

            HttpSession session = request.getSession();
            session.setAttribute("socialInfo", socialInfo);

            System.out.println("-> 신규 회원 가입 페이지(/register)로 이동");
            getRedirectStrategy().sendRedirect(request, response, "/register");

        } else {
            // (A) 기존 회원 -> JWT 토큰 발급 후 메인으로

            // 1. 아까 Service에서 담아둔 DB 아이디 꺼내기
            String username = (String) attributes.get("internalUsername");

            // 2. 토큰 생성 (JwtUtil 사용)
            // (주의: JwtUtil의 메서드명이 generateToken이 맞는지 확인하세요!)
            String token = jwtUtil.generateToken(username);

            System.out.println("✅ JWT 토큰 발급 완료: " + token.substring(0, 10) + "...");
            System.out.println("-> 메인 페이지로 토큰과 함께 이동");

            // 3. 토큰을 URL 파라미터로 붙여서 리다이렉트
            // (프론트엔드에서 이 토큰을 잡아서 저장해야 함)
            getRedirectStrategy().sendRedirect(request, response, "/?token=" + token);
        }
    }
}