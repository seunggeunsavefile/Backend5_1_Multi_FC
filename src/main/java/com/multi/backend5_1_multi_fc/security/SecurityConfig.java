package com.multi.backend5_1_multi_fc.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        /*  =============================
                         *   1) 인증 없이 허용되는 API
                         *  ============================= */

                        // User API permitAll
                        .requestMatchers("/api/users/login", "/api/users/signup").permitAll()
                        .requestMatchers("/api/users/check-username", "/api/users/check-email", "/api/users/check-nickname").permitAll()
                        .requestMatchers("/api/users/find-id", "/api/users/reset-password/**").permitAll()

                        // match 모듈에서 허용해야 하는 API들
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()

                        /* =============================
                         *   2) 정적 리소스
                         * ============================= */
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/favicon.ico").permitAll()

                        /* =============================
                         *   3) HTML 페이지 허용
                         * ============================= */
                        .requestMatchers(
                                "/", "/login", "/register", "/forgot-password",
                                "/mypage", "/profile/edit", "/friends", "/chat",
                                "/fields", "/stadium/detail",
                                "/schedule", "/schedule/add", "/schedule/detail/**", "/schedule/private/detail",
                                "/community", "/community/write", "/community/detail/**",
                                "/reviews/write",
                                "/team/create", "/team/manage", "/team/invite", "/team-edit"
                        ).permitAll()

                        //[추가] 준호님 요청
                        .requestMatchers("/chat", "/chat/**").permitAll()
                        .requestMatchers("/chatroom/**").permitAll()
                        .requestMatchers("/api/chat/**").permitAll()
                        .requestMatchers("/notifications","/notifications/**").permitAll()
                        .requestMatchers("/api/notifications/**").permitAll()
                        .requestMatchers("/api/users/me", "/api/user/**").permitAll()

                        /* =============================
                         *   4) 나머지는 인증 필요
                         * ============================= */
                        .anyRequest().authenticated()
                )


                // (⭐️ 5. [신규 기능] OAuth2 소셜 로그인 설정 추가)
                .oauth2Login(oauth2 -> oauth2
                        // (1) 소셜 로그인을 위한 우리 커스텀 로그인 페이지
                        .loginPage("/login")
                        // (2) 로그인 성공 시 실행할 서비스 (여기서 이메일 체크)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // (3) 로그인 성공 후 처리할 핸들러 (여기서 /register 또는 / 로 분기)
                        .successHandler(oAuth2LoginSuccessHandler)
                        // (4) 로그인 실패 시
                        .failureUrl("/login?error=true")
                )

                /* JWT 필터 적용 */
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}