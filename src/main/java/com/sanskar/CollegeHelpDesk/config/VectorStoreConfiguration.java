package com.sanskar.CollegeHelpDesk.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

// Previously I used langchain4j's EmbeddingStore which is lower level than VectorStore
// Let Redis VectorStore be auto configured, and I will configure Elasticsearch VectorStore myself as I need to set the index name and dimensions
@Configuration
public class VectorStoreConfiguration {
    @Bean
    public RestClient restClient(
            @Value("${elasticsearch.host}") String host,
            @Value("${elasticsearch.port}") int port
    ) {
        // no need to mention api key as check environment config in docker compose
        return RestClient.builder(new HttpHost(host, port, "http")).build();
    }

    @Bean("notice-index")
    public VectorStore noticeIndexVectorStore(RestClient restClient,
                                              EmbeddingModel embeddingModel,
                                              @Value("${embedding.dimensions}") int dimensions
    ) {
        ElasticsearchVectorStoreOptions vectorStoreOptions = new ElasticsearchVectorStoreOptions();
        vectorStoreOptions.setDimensions(dimensions);
        vectorStoreOptions.setIndexName("notice-index");
        // uses EmbeddingModel to embed directly rather than me implementing a whole embedding service
        return ElasticsearchVectorStore.builder(restClient, embeddingModel)
                .options(vectorStoreOptions)
                .initializeSchema(true) // create index if not exists
                .build();
    }

    @Bean("faculty-index")
    public VectorStore facultyIndexVectorStore(RestClient restClient,
                                               EmbeddingModel embeddingModel,
                                               @Value("${embedding.dimensions}") int dimensions
    ) {
        ElasticsearchVectorStoreOptions vectorStoreOptions = new ElasticsearchVectorStoreOptions();
        vectorStoreOptions.setDimensions(dimensions);
        vectorStoreOptions.setIndexName("faculty-index");
        return ElasticsearchVectorStore.builder(restClient, embeddingModel)
                .options(vectorStoreOptions)
                .initializeSchema(true) // create index if not exists
                .build();
    }

    @Bean("scholarship-index")
    public VectorStore scholarshipIndexVectorStore(RestClient restClient,
                                               EmbeddingModel embeddingModel,
                                               @Value("${embedding.dimensions}") int dimensions
    ) {
        ElasticsearchVectorStoreOptions vectorStoreOptions = new ElasticsearchVectorStoreOptions();
        vectorStoreOptions.setDimensions(dimensions);
        vectorStoreOptions.setIndexName("scholarship-index");
        return ElasticsearchVectorStore.builder(restClient, embeddingModel)
                .options(vectorStoreOptions)
                .initializeSchema(true) // create index if not exists
                .build();
    }

    @Bean
    public JedisPooled jedisPooled(@Value("${redis.host}") String host, @Value("${redis.port}") int port) {
        return new JedisPooled(host, port);
    }

    @Bean("cache-index")
    public VectorStore redisVectorStore(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .contentFieldName("query")
                .metadataFields(RedisVectorStore.MetadataField.text("answer"))
                .indexName("cache-index")
                .initializeSchema(true)
                .build();
    }

}
