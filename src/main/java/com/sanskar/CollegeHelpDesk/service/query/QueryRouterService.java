package com.sanskar.CollegeHelpDesk.service.query;

import com.sanskar.CollegeHelpDesk.model.ModelName;
import com.sanskar.CollegeHelpDesk.model.ResourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class QueryRouterService {

    @Qualifier("queryClassificationChatClient")
    @Autowired
    private ChatClient chatClient;

    @Value("classpath:/prompts/query_classifier_system_prompt.st")
    private Resource systemPromptResource;

    public Set<ResourceType> detectTabs(String transformedQuery, String modelName){
        log.info("QueryRouterService called");
        Instant start = Instant.now();
        // end quote treated as base indentation level
        // if text on left of end quote → no extra indentation
        // if text on right of end quote → means space included
        PromptTemplate promptTemplate = PromptTemplate.builder()
                .resource(systemPromptResource)
                .build();
        String prompt = promptTemplate.render(Map.of());


        String rawResponse = chatClient
                .prompt()
                .options(ChatOptions.builder()
                        .model(modelName)
                        .build())
                .system(prompt)
                .user(transformedQuery)
                .call()
                .content();
        Instant end = Instant.now();
        log.info("QueryRouterService llm call latency: {} ms", end.toEpochMilli() - start.toEpochMilli());
        return parse(rawResponse);
    }

    private Set<ResourceType> parse(String raw){

        if (raw == null || raw.isBlank()) {
            return fallback();
        }
        Set<ResourceType> set = new HashSet<>();
        for (String s : raw.split(",")) {
            try {
                set.add(ResourceType.valueOf(s.trim().toUpperCase()));
            } catch (Exception ignored) {}
        }
        if (set.isEmpty()) {
            return fallback();
        }
        return set;
    }
    Set<ResourceType> fallback() {
        return Set.of(
                ResourceType.FACULTY,
                ResourceType.NOTICE,
                ResourceType.SCHOLARDOC
        );
    }
}
