//package com.multi.backend5_1_multi_fc.security;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.messaging.support.MessageHeaderAccessor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//@Order(Ordered.HIGHEST_PRECEDENCE + 99)
//public class JwtChannelInterceptor implements ChannelInterceptor {
//
//    private final JwtUtil jwtUtil;
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        log.info("WebSocket Message: Command = {}", accessor.getCommand());
//
//        if(StompCommand.CONNECT.equals(accessor.getCommand())){
//            log.info("WebSocket CONNECT 요청 - JWT 검증 시작");
//            String authHeader = accessor.getFirstNativeHeader("Authorization");
//
//            if( authHeader != null && authHeader.startsWith("Bearer ") ){
//                String token = authHeader.substring(7);
//                log.info("추출된 토큰: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
//
//                try {
//                    String username = jwtUtil.extractUsername(token);
//                    log.info("토큰에서 추출한 사용자: {}", username);
//                    if(jwtUtil.validateToken(token, username)){
//                        log.info("JWT 검증 성공 - 사용자: {}", username);
//                        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                                username,
//                                null,
//                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
//                        );
//
//                        accessor.setUser(authentication);
//                        log.info("WebSocket 세션에 인증 정보 설정 완료: {}", username);
//                    } else {
//                        log.error("JWT 토큰 검증 실패 - 만료되었거나 유효하지 않음");
//                        throw new IllegalArgumentException("Invalid or expired JWT token");
//                    }
//                } catch (Exception e){
//                    log.error("JWT 토큰 처리 중 오류: {}", e.getMessage(), e);
//                    throw new IllegalArgumentException("JWT authentication failed: " + e.getMessage(), e);
//                }
//            } else {
//                log.error("Authorization 헤더가 없거나 형식이 잘못됨");
//                throw new IllegalArgumentException("Missing or invalid Authorization header");
//            }
//        }
//        if(accessor.getUser() == null && !StompCommand.CONNECT.equals(accessor.getCommand()) && !StompCommand.DISCONNECT.equals(accessor.getCommand())){
//            log.warn("인증되지 않은 사용자의 메시지 시도: Command = {}", accessor.getCommand());
//        }
//        return message;
//    }
//
//    @Override
//    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if(StompCommand.CONNECT.equals(accessor.getCommand())){
//            String username = accessor.getUser() != null ? accessor.getUser().getName() : "unknown";
//            log.info("WebSocket 연결 완료: 사용자 = {}, 전송 여부 = {}", username, sent);
//        } else if(StompCommand.DISCONNECT.equals(accessor.getCommand())){
//            String username = accessor.getUser() != null ? accessor.getUser().getName() : "unknown";
//            log.info("WebSocket 연결 종료: 사용자 = {}", username);
//        }
//    }
//
//    @Override
//    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
//        if(ex != null){
//            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//            log.error("WebSocket 메시지 전송 실패: Command = {}, Error = {}", accessor.getCommand(), ex.getMessage());
//        }
//    }
//}
//
package com.multi.backend5_1_multi_fc.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // 프레임 진입 확인
        System.out.println("[INTERCEPTOR] preSend called, Command = " + accessor.getCommand());

        if(StompCommand.CONNECT.equals(accessor.getCommand())){
            log.info("WebSocket CONNECT 요청 - JWT 검증 시작");
            System.out.println("WebSocket CONNECT 요청 - JWT 검증 시작");
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if( authHeader != null && authHeader.startsWith("Bearer ") ){
                String token = authHeader.substring(7);
                log.info("추출된 토큰: {}", token.substring(0, Math.min(token.length(), 20)) + "...");

                try {
                    String username = jwtUtil.extractUsername(token);
                    log.info("토큰에서 추출한 사용자: {}", username);
                    System.out.println("username = " + username + " ");
                    if(jwtUtil.validateToken(token, username)){
                        log.info("JWT 검증 성공 - 사용자: {}", username);
                        System.out.println("username = " + username + " ");
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                        accessor.setUser(authentication);
                        log.info("WebSocket 세션에 인증 정보 설정 완료: {}", username);
                        System.out.println("세션 인증 정보 설정 완료" + username + " ");
                    } else {
                        log.error("JWT 토큰 검증 실패 - 만료되었거나 유효하지 않음");
                        System.out.println("JWT 토큰 검증 실패 - 만료되었거나 유효하지 않음");
                        throw new IllegalArgumentException("Invalid or expired JWT token");
                    }
                } catch (Exception e){
                    log.error("JWT 토큰 처리 중 오류: {}", e.getMessage(), e);
                    throw new IllegalArgumentException("JWT authentication failed: " + e.getMessage(), e);
                }
            } else {
                log.error("Authorization 헤더가 없거나 형식이 잘못됨");
                System.out.println("Authorization 헤더가 없거나 형식이 잘못됨");
                throw new IllegalArgumentException("Missing or invalid Authorization header");
            }
        }
        if(accessor.getUser() == null && !StompCommand.CONNECT.equals(accessor.getCommand()) && !StompCommand.DISCONNECT.equals(accessor.getCommand())){
            log.warn("인증되지 않은 사용자의 메시지 시도: Command = {}", accessor.getCommand());
        }
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if(StompCommand.CONNECT.equals(accessor.getCommand())){
            String username = accessor.getUser() != null ? accessor.getUser().getName() : "unknown";
            log.info("WebSocket 연결 완료: 사용자 = {}, 전송 여부 = {}", username, sent);
        } else if(StompCommand.DISCONNECT.equals(accessor.getCommand())){
            String username = accessor.getUser() != null ? accessor.getUser().getName() : "unknown";
            log.info("WebSocket 연결 종료: 사용자 = {}", username);
        }
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        if(ex != null){
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            log.error("WebSocket 메시지 전송 실패: Command = {}, Error = {}", accessor.getCommand(), ex.getMessage());
        }
    }
}