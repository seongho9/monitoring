package com.fishingboat.config.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
@Data
@RequiredArgsConstructor
@Configuration
@Slf4j
public class DomainConfig {

    private final RedisConfig _RedisInfo;
    private final MonitoringConfig _MonitoringInfo;
}
