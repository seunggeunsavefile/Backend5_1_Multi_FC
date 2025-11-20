// Kakao 장소 API 응답 DTO
package com.multi.backend5_1_multi_fc.match.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPlaceRes {
    private String id;            // 카카오 place_id
    private String placeName;
    private String addressName;
    private double latitude;
    private double longitude;
}
