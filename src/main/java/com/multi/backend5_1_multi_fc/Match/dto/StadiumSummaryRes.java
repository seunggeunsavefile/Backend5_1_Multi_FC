// 경기장 요약 정보 DTO
package com.multi.backend5_1_multi_fc.match.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StadiumSummaryRes {
    private Long stadiumId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
}
