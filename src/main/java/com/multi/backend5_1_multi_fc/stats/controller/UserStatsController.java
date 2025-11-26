package com.multi.backend5_1_multi_fc.stats.controller;

import com.multi.backend5_1_multi_fc.stats.dto.UserStatsDto;
import com.multi.backend5_1_multi_fc.stats.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class UserStatsController {

    private final UserStatsService userStatsService;

    //내 매칭률 조회
    @GetMapping("/matching-rate/me")
    public ResponseEntity<UserStatsDto.MatchingRateResponse> getMyMatchingRate() {
        //저장된 인증 정보 꺼내기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = auth.getName();
        UserStatsDto.MatchingRateResponse response =
                userStatsService.getMyMatchingRate(username);

        return ResponseEntity.ok(response);
    }
}
