package com.multi.backend5_1_multi_fc.security;

import com.multi.backend5_1_multi_fc.user.dto.UserDto;
import com.multi.backend5_1_multi_fc.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserMapper userMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email;
        String name;
        String picture;
        String userNameAttributeName;

        // 1. 서비스별 속성 추출
        if (registrationId.equals("google")) {
            userNameAttributeName = "sub";
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            picture = (String) attributes.get("picture");
        } else if (registrationId.equals("kakao")) {
            userNameAttributeName = "id";
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            email = (String) kakaoAccount.get("email");
            name = (String) profile.get("nickname");
            picture = (String) profile.get("profile_image_url");
        } else {
            throw new OAuth2AuthenticationException("Unsupported registrationId: " + registrationId);
        }


        System.out.println("========== SSO 로그인 시도 ==========");
        System.out.println("플랫폼: " + registrationId);
        System.out.println("이메일: " + email);

        // 2. DB에서 이메일로 사용자 조회
        UserDto user = userMapper.findUserByEmail(email);

        if (user != null) {
            // (A) 이미 가입된 회원 (로그인 성공)
            System.out.println("✅ 기존 회원 확인됨! ID: " + user.getUsername());

            Map<String, Object> userAttributes = new HashMap<>(attributes);

            userAttributes.put("internalUsername", user.getUsername());

            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                    userAttributes,
                    userNameAttributeName
            );
        }

        // (B) 신규 회원 (회원가입 필요)
        System.out.println("🆕 신규 회원입니다. 회원가입 페이지로 이동합니다.");

        Map<String, Object> customAttributes = new HashMap<>(attributes);
        customAttributes.put("email", email);
        customAttributes.put("name", name);
        customAttributes.put("picture", picture);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_PRE_REGISTER")),
                customAttributes,
                userNameAttributeName
        );
    }
}