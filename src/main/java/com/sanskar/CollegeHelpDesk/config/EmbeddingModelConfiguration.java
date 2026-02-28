package com.sanskar.CollegeHelpDesk.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingModelConfiguration {

    @Bean // return generic EmbeddingModel interface
    public EmbeddingModel embeddingModel(
            @Value("${ollama.host}") String host,
            @Value("${ollama.port}") int port
    ) {
        return OllamaEmbeddingModel.builder()
                .baseUrl("http://" + host + ":" + port)   // inside docker network
                .modelName("nomic-embed-text")            // recommended embedding model
                .build();
    }
}

