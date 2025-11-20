// 경기장 검색 및 DB 저장 REST API
package com.multi.backend5_1_multi_fc.match.controller;

import com.multi.backend5_1_multi_fc.match.dto.StadiumSummaryRes;
import com.multi.backend5_1_multi_fc.match.service.StadiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stadiums")
@RequiredArgsConstructor
public class StadiumController {

    private final StadiumService stadiumService;

    @GetMapping("/search")
    public List<StadiumSummaryRes> search(@RequestParam String keyword) {
        return stadiumService.searchAndSave(keyword);
    }

    @GetMapping
    public List<StadiumSummaryRes> list() {
        return stadiumService.listAll();
    }
}
