package com.multi.backend5_1_multi_fc.match.mapper;

import com.multi.backend5_1_multi_fc.match.domain.Stadium;
import com.multi.backend5_1_multi_fc.match.dto.StadiumSummaryRes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StadiumMapper {
    List<StadiumSummaryRes> findAll();
    void insertFromKakao(Stadium stadium);
    int countByName(String name);
    Stadium findByName(String name);   // 🔥 추가됨
}
