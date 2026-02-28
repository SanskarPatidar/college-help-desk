package com.sanskar.CollegeHelpDesk.service.query;

import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class LlmQueryService {
    // private final ChatModel chatModel; // lower level abstraction than ChatClient
    private final ChatClient chatClient;

    public String answer(String prompt) {
        log.info("LlmQueryService received prompt:\n{}", prompt);
        Instant start = Instant.now();
        String answer = chatClient.prompt(prompt).call().content();
        Instant end = Instant.now();
        log.info("LlmQueryService llm latency: {} ms", end.toEpochMilli() - start.toEpochMilli());
        return answer;
    }
}
