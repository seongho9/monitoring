package com.fishingboat.config.api;

import lombok.Data;

@Data
public class PullRequest {
    String name;
    String baseUrl;
    String path;
    String unit;
    int interval;
}
