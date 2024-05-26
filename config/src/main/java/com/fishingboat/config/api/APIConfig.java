package com.fishingboat.config.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@RequiredArgsConstructor
public class APIConfig {

    private final RestTemplateConfig _RestTemplate;
    private final PullConfig _PullConfig;
}
