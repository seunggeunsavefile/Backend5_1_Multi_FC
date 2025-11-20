// Kakao Maps API로 장소 검색 서비스
package com.multi.backend5_1_multi_fc.match.service;

import com.multi.backend5_1_multi_fc.match.dto.KakaoPlaceRes;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KakaoMapService {

    @Value("${kakao.rest-key}")
    private String kakaoRestKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<KakaoPlaceRes> searchPlacesByArea(String query) {
        try {
            String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + query;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoRestKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JSONObject body = new JSONObject(response.getBody());
            JSONArray docs = body.getJSONArray("documents");

            List<KakaoPlaceRes> results = new ArrayList<>();

            for (int i = 0; i < docs.length(); i++) {
                JSONObject d = docs.getJSONObject(i);

                results.add(new KakaoPlaceRes(
                        d.getString("id"),
                        d.getString("place_name"),
                        d.optString("road_address_name"),
                        d.optDouble("y"),
                        d.optDouble("x")
                ));
            }

            return results;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
