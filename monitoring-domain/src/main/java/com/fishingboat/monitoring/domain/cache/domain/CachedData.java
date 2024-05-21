package com.fishingboat.monitoring.domain.cache.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
public class CachedData {

    @Id
    private String id;
    private String value;
}
