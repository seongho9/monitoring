package com.fishingboat.monitoring.api.config;

import com.fishingboat.config.api.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Configuration
public class RestTemplateBeanInject {

    private final RestTemplateConfig _RestTemplateInfo;

    public RestTemplateBeanInject(@Autowired APIConfig config) {

        this._RestTemplateInfo = config.get_RestTemplate();
    }

    @Bean
    public PoolingHttpClientConnectionManager connectionManager(){

        log.info("max connection : {}", _RestTemplateInfo.getMaxConnection());
        log.info("route per connection : {}", _RestTemplateInfo.getConnectionPerRoute());

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

        connectionManager.setMaxTotal(_RestTemplateInfo.getMaxConnection());
        connectionManager.setDefaultMaxPerRoute(_RestTemplateInfo.getConnectionPerRoute());
        
        return connectionManager;
    }
    @Bean
    public HttpClient httpClient(PoolingHttpClientConnectionManager connectionManager){

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .evictIdleConnections(TimeValue.ofMicroseconds(_RestTemplateInfo.getEvictIdleConnections()))
                .build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory factory(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(_RestTemplateInfo.getSessionTimeout());
        factory.setHttpClient(httpClient);

        return factory;
    }

    @Bean
    public RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory factory) {

        return new RestTemplate(factory);
    }
}
