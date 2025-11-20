package com.multi.backend5_1_multi_fc.match.service;

import com.multi.backend5_1_multi_fc.match.domain.Stadium;
import com.multi.backend5_1_multi_fc.match.dto.KakaoPlaceRes;
import com.multi.backend5_1_multi_fc.match.dto.StadiumSummaryRes;
import com.multi.backend5_1_multi_fc.match.mapper.StadiumMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StadiumService {

    private final KakaoMapService kakaoMapService;
    private final StadiumMapper stadiumMapper;

    public List<StadiumSummaryRes> listAll() {
        return stadiumMapper.findAll();
    }

    /** 검색 후 DB 저장 + 발생한 데이터만 반환 */
    @Transactional
    public List<StadiumSummaryRes> searchAndSave(String areaKeyword) {

        // 키워드 생성
        String[] keywords = {
                areaKeyword + " 풋살장",
                areaKeyword + " 축구장",
                areaKeyword + " 풋볼"
        };

        Map<String, KakaoPlaceRes> unique = new HashMap<>();

        // 카카오 API 검색 결과 중복 제거
        for (String key : keywords) {
            List<KakaoPlaceRes> found = kakaoMapService.searchPlacesByArea(key);
            for (KakaoPlaceRes p : found) {
                unique.put(p.getId(), p);
            }
        }

        List<StadiumSummaryRes> resultList = new ArrayList<>();

        // DB 저장 + stadiumId 가져오기
        for (KakaoPlaceRes p : unique.values()) {

            Stadium stadium;

            // 이름 중복 확인
            if (stadiumMapper.countByName(p.getPlaceName()) == 0) {

                // 신규 등록
                stadium = new Stadium();
                stadium.setName(p.getPlaceName());
                stadium.setAddress(p.getAddressName());
                stadium.setLatitude(p.getLatitude());
                stadium.setLongitude(p.getLongitude());

                stadiumMapper.insertFromKakao(stadium);  // useGeneratedKeys 로 stadiumId 자동 주입됨

            } else {
                // 이미 존재 → 기존 DB 값 가져오기
                stadium = stadiumMapper.findByName(p.getPlaceName());
            }

            // 반환 리스트에 stadiumId 포함해 저장
            resultList.add(new StadiumSummaryRes(
                    stadium.getStadiumId(),
                    stadium.getName(),
                    stadium.getAddress(),
                    stadium.getLatitude(),
                    stadium.getLongitude()
            ));
        }

        return resultList;
    }
}
