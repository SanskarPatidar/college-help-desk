package com.sanskar.CollegeHelpDesk.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DynamicTranslationTransformer {

    private final PromptTemplate template;
    private final ChatClient.Builder builder;

    public DynamicTranslationTransformer(
            ChatClient.Builder builder,
            @Value("classpath:/prompts/query_translator_system_prompt.st") Resource resource
    ) {
        this.builder = builder;
        this.template = PromptTemplate.builder()
                .resource(resource)
                .build();
    }

    public String transform(String query, String modelName) {
        System.out.println(query);
        String prompt = template.render(Map.of(
                "query", query,
                "targetLanguage", "ENGLISH"
        ));

        ChatClient client = builder
                .defaultOptions(ChatOptions.builder()
                        .model(modelName)
                        .temperature(0.1)
                        .build())
                .build();

        return client.prompt()
                .user(prompt)
                .call()
                .content();
    }
}