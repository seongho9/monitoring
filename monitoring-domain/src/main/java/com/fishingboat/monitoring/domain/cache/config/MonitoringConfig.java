package com.fishingboat.monitoring.domain.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "monitoring")
public class MonitoringConfig {
    
    String delimiter;
    
    // 추후에 관련 데이터 추가 예정
}
