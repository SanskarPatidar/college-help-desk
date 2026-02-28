package com.sanskar.CollegeHelpDesk.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatModelConfiguration {

    @Bean
    public ChatModel ollamaChatModel(
            @Value("${ollama.host}") String host,
            @Value("${ollama.port}") int port
    ) {
        return OllamaChatModel.builder()
                .baseUrl("http://" + host + ":" + port)
                .modelName("qwen:4b")
                .temperature(0.2) // low hallucination for RAG
                .maxRetries(0)
                .build();
    }
}
