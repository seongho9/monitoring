package com.fishingboat.config.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis.cluster")
public class RedisConfig {
    private int maxRedirects;
    private String password;
    private Long refreshPeriod;
    private String connectIp;
    private List<String> nodes;

}
