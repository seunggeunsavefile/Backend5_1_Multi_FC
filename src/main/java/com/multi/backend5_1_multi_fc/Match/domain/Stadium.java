// 경기장 엔티티
package com.multi.backend5_1_multi_fc.match.domain;

import lombok.Data;

@Data
public class Stadium {
    private Long stadiumId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
}
