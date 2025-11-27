package com.ssafy.backend.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatisConfig는 MyBatis 설정을 담당하는 클래스입니다.
 * @MapperScan을 통해 Mapper 인터페이스를 자동으로 스캔합니다.
 */
@MapperScan("com.ssafy.backend.goods.model")
@Configuration
public class MyBatisConfig {
}
