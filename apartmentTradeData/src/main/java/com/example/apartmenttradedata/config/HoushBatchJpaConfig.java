package com.example.apartmenttradedata.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // 생성일시 , 수정일시를 자동으로 넣어줄 수 있음
public class HoushBatchJpaConfig {

}
