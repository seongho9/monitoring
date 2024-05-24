package com.fishingboat.monitoring.domain.cache.dto;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CachedDataDTO {

    private String delimiter;
    private String domain;
    private String time;
    private String value;

    public String getKey(){

        return String.format("%s%s%s", this.domain, this.delimiter,this.time);
    }

    public String getValue(){
        return this.value;
    }
}
