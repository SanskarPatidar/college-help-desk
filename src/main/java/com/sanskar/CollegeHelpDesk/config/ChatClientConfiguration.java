package com.sanskar.CollegeHelpDesk.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
/*
* Yaml configurations are default base configurations
* Below configurations override the default
* default yaml config -> config class -> at runtime options change
* each overrides the previous
 */

@Configuration
@RequiredArgsConstructor
public class ChatClientConfiguration {

    SafeGuardAdvisor safeGuardAdvisor = SafeGuardAdvisor.builder()
            .sensitiveWords(List.of("Bomb", "Attack", "Kill", "Hate", "Terror"))
            .build(); // can be used to filter out sensitive content from prompt or response
    // recursive advisor: when you need to repeatedly call the LLM until a certain condition is met

    @Bean(name = "finalQueryChatClient")
    public ChatClient finalQueryChatClient(ChatClient.Builder builder, MetadataLoggerAdvisor metadataLoggerAdvisor) {
        // Final Query chat client configuration

        // internally by default uses StTemplateRenderer, can also provide custom implementation if needed
        // can dynamically variable


        // advisors are interceptor before prompt reaches llm(per request and pre response)
        // will read conversationId from metadata
        // just need to  pass conversationId per request in service layer
        // PromptChtMemoryAdvisor storing full query with template, but I want to store only incoming user query
//        PromptChatMemoryAdvisor promptAdvisor = PromptChatMemoryAdvisor.builder(chatMemory) // requires chat memory to add conversation history to the prompt as system instructions
//                .systemPromptTemplate(promptTemplate)
//                .build();

        return builder
                .defaultOptions(ChatOptions.builder()
                        .temperature(0.3)
                        .topP(0.9) // avoids unnecessary randomness
//                        .frequencyPenalty(0.2) // avoid repetition
//                        .presencePenalty(0.1) // slight diversity
                        .maxTokens(512)
                        .build())
                .defaultAdvisors(safeGuardAdvisor, metadataLoggerAdvisor)
                .build();
    }

    @Bean("queryClassificationChatClient")
    public ChatClient queryClassificationChatClient(ChatClient.Builder builder) {
        return builder
                .defaultOptions(
                        ChatOptions.builder()
                                .temperature(0.0) // strict deterministic
                                .topP(1.0)
//                                .frequencyPenalty(0.0)
//                                .presencePenalty(0.0)
                                .build()
                )
                .defaultAdvisors(safeGuardAdvisor)
                .build();
    }

    // Default embedding model configurations are enough, no need to override for now, can always create new bean if needed with different options
    @Bean
    public EmbeddingModel embeddingModel(RestTemplate restTemplate) {
        return new GeminiEmbeddingModel(
                restTemplate,
                System.getenv("GOOGLE_API_KEY")
        );
    }

}
