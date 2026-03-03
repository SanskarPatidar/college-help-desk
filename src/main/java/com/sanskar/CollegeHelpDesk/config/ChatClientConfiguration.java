package com.sanskar.CollegeHelpDesk.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ChatClientConfiguration {

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
        SafeGuardAdvisor safeGuardAdvisor = SafeGuardAdvisor.builder()
                .sensitiveWords(List.of())
                .build(); // can be used to filter out sensitive content from prompt or response
        // recursive advisor: when you need to repeatedly call the LLM until a certain condition is met
        return builder
                .defaultOptions(ChatOptions.builder()
                        .temperature(0.2)
                        .build())
                .defaultAdvisors(safeGuardAdvisor, metadataLoggerAdvisor)
                .build();
    }

    @Bean("queryClassificationChatClient")
    public ChatClient queryClassificationChatClient(ChatClient.Builder builder) {
        return builder
                .defaultOptions(
                        ChatOptions.builder()
                                .temperature(0.1)
                                .topP(1.0)
                                .frequencyPenalty(0.0)
                                .presencePenalty(0.0)
                                .build()
                )
                .build();

    }


}
