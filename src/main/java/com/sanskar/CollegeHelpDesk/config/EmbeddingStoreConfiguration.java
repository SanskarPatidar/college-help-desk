package com.sanskar.CollegeHelpDesk.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingStoreConfiguration {
    @Bean
    public RestClient restClient(
            @Value("${elasticsearch.host}") String host,
            @Value("${elasticsearch.port}") int port
    ) {
        // no need to mention api key as check environment config in docker compose
        return RestClient.builder(new HttpHost(host, port, "http")).build();
    }
}
