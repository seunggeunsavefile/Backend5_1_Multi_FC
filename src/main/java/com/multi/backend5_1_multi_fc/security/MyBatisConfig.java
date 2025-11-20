//Mybatis 매퍼 스캔 설정
package com.multi.backend5_1_multi_fc.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.multi.backend5_1_multi_fc")
public class MyBatisConfig { }
