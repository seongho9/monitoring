package com.fishingboat.config.api;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.web.rest-template")
public class RestTemplateConfig {
    int maxConnection;
    int connectionPerRoute;
    int evictIdleConnections;
    int sessionTimeout;
}
