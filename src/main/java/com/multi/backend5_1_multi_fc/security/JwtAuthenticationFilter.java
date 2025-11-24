////package com.multi.backend5_1_multi_fc.security;
////
////import jakarta.servlet.FilterChain;
////import jakarta.servlet.ServletException;
////import jakarta.servlet.http.HttpServletRequest;
////import jakarta.servlet.http.HttpServletResponse;
////import lombok.RequiredArgsConstructor;
////import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
////import org.springframework.security.core.context.SecurityContextHolder;
////import org.springframework.security.core.userdetails.UserDetails;
////import org.springframework.security.core.userdetails.UserDetailsService;
////import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
////import org.springframework.stereotype.Component;
////import org.springframework.web.filter.OncePerRequestFilter;
////
////import java.io.IOException;
////
////@Component
////@RequiredArgsConstructor
////public class JwtAuthenticationFilter extends OncePerRequestFilter {
////
////    private final JwtUtil jwtUtil;
////    private final UserDetailsService userDetailsService; // UserService가 주입됨
////
////    @Override
////    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
////            throws ServletException, IOException {
////
////        final String authHeader = request.getHeader("Authorization");
////        final String jwt;
////        final String username;
////
////        // 1. 헤더가 없거나 "Bearer "로 시작하지 않으면 필터를 그냥 통과
////        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
////            filterChain.doFilter(request, response);
////            return;
////        }
////
////        jwt = authHeader.substring(7); // "Bearer " 다음부터 토큰 추출
////
////        try {
////            username = jwtUtil.extractUsername(jwt);
////
////            // 2. username이 존재하고, 아직 SecurityContext에 인증 정보가 없다면
////            if (username != null) {
////
////                // 3. DB에서 사용자 정보 조회
////                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
////
////                // 4. 토큰이 유효하다면
////                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
////                    // 5. Spring Security가 사용할 인증 토큰 생성
////                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
////                            userDetails,
////                            null,
////                            userDetails.getAuthorities()
////                    );
////                    authToken.setDetails(
////                            new WebAuthenticationDetailsSource().buildDetails(request)
////                    );
////
////                    // 6. SecurityContext에 인증 정보 저장
////                    SecurityContextHolder.getContext().setAuthentication(authToken);
////                }
////            }
////        } catch (Exception e) {
////            // 토큰이 유효하지 않다면 SecurityContext
////            SecurityContextHolder.clearContext();
////        }
////
////        filterChain.doFilter(request, response);
//
////    }
////}
//package com.multi.backend5_1_multi_fc.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtUtil jwtUtil;
//    private final UserDetailsService userDetailsService; // UserService가 주입됨
//
//    public JwtAuthenticationFilter(
//            JwtUtil jwtUtil,
//            @Qualifier("customUserDetailsService") UserDetailsService userDetailsService
//    ) {
//        this.jwtUtil = jwtUtil;
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        final String authHeader = request.getHeader("Authorization");
//        final String jwt;
//        final String username;
//
//        // 1. 헤더가 없거나 "Bearer "로 시작하지 않으면 필터를 그냥 통과
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        jwt = authHeader.substring(7); // "Bearer " 다음부터 토큰 추출
//
//        try {
//            username = jwtUtil.extractUsername(jwt);
//
//            // 2. username이 존재하고, 아직 SecurityContext에 인증 정보가 없다면
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//                // 3. DB에서 사용자 정보 조회
//                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//
//                // 4. 토큰이 유효하다면
//                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
//                    // 5. Spring Security가 사용할 인증 토큰 생성
//                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            userDetails.getAuthorities()
//                    );
//                    authToken.setDetails(
//                            new WebAuthenticationDetailsSource().buildDetails(request)
//                    );
//
//                    // 6. SecurityContext에 인증 정보 저장
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                }
//            }
//        } catch (Exception e) {
//            // 토큰이 유효하지 않다면 SecurityContext
//            SecurityContextHolder.clearContext();
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}

package com.multi.backend5_1_multi_fc.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService; // UserService가 주입됨

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            @Qualifier("customUserDetailsService") UserDetailsService userDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. 헤더가 없거나 "Bearer "로 시작하지 않으면 필터를 그냥 통과
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // "Bearer " 다음부터 토큰 추출

        try {
            username = jwtUtil.extractUsername(jwt);

            // 2. username이 존재하고, 아직 SecurityContext에 인증 정보가 없다면
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 3. DB에서 사용자 정보 조회
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 4. 토큰이 유효하다면
                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    // 5. Spring Security가 사용할 인증 토큰 생성
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 6. SecurityContext에 인증 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // 토큰이 유효하지 않다면 SecurityContext
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}