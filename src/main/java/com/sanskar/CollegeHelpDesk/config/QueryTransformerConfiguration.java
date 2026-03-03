package com.sanskar.CollegeHelpDesk.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class QueryTransformerConfiguration {

    @Value("classpath:/prompts/query_compressor_system_prompt.st")
    private Resource compressorSystemPromptResource;

    @Value("classpath:/prompts/query_translator_system_prompt.st")
    private Resource translatorSystemPromptResource;

    @Bean
    public CompressionQueryTransformer compressionQueryTransformer(ChatClient.Builder builder) {
        PromptTemplate customPromptTemplate = PromptTemplate.builder()
                .resource(compressorSystemPromptResource)
                .build();
        return CompressionQueryTransformer.builder()
                .promptTemplate(customPromptTemplate)
                .chatClientBuilder(builder.defaultOptions(
                        ChatOptions.builder()
                                .temperature(0.1)
                                .build())
                )
                .build();
    }

    @Bean
    public TranslationQueryTransformer translationQueryTransformer(ChatClient.Builder builder) {
        PromptTemplate customPromptTemplate = PromptTemplate.builder()
                .resource(translatorSystemPromptResource)
                .build();
        return TranslationQueryTransformer.builder()
                .promptTemplate(customPromptTemplate)
                .targetLanguage("ENGLISH")
                .chatClientBuilder(builder.defaultOptions(
                        ChatOptions.builder()
                                .temperature(0.1)
                                .build())
                )
                .build();
    }
}
