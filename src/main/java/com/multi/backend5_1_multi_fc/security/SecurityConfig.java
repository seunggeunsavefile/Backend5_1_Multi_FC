package com.multi.backend5_1_multi_fc.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // (1) API: 인증 없이 허용
                        .requestMatchers("/login", "/signup", "/api/auth/**").permitAll()
                        .requestMatchers("/chat", "/chat/**").permitAll()
                        .requestMatchers("/chatroom/**").authenticated()
                        .requestMatchers("/api/chatroom/**").authenticated()
                        .requestMatchers("/notifications","/notifications/**").permitAll()
                        .requestMatchers("/api/notifications/**").authenticated()
                        .requestMatchers("/api/users/me", "/api/user/**").authenticated()
                        .requestMatchers("/api/friends/**").authenticated()
                        .requestMatchers("/api/users/login", "/api/users/signup").permitAll()
                        .requestMatchers("/api/users/check-username", "/api/users/check-email", "/api/users/check-nickname").permitAll()
                        .requestMatchers("/api/users/find-id", "/api/users/reset-password/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/community/posts",
                                "/api/community/posts/**"
                        ).permitAll()

                        // (2) ★★★ 정적 리소스: 인증 없이 허용 ★★★
                        .requestMatchers("/css/**", "/images/**", "/js/**").permitAll()

                        // (3) HTML 페이지: 인증 없이 허용
                        .requestMatchers(
                                "/", "/login", "/register", "/forgot-password",
                                "/mypage", "/profile/edit", "/friends", "/chat",
                                "/fields", "/stadium/detail",
                                "/schedule", "/schedule/add", "/schedule/detail/**", "/schedule/private/detail",
                                "/community", "/community/write", "/community/detail/**",
                                "/reviews/write",
                                "/team/create", "/team/manage", "/team/invite", "/team-edit"
                        ).permitAll()

                        // (4) 그 외 모든 요청 (주로 API)은 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}