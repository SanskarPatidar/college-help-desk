package com.sanskar.CollegeHelpDesk.service.query;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SystemMessageBuilderService {
    @Autowired
    private ChatMemory chatMemory;
    @Value("${chat-history.max-messages:2}")
    private int MAX_CHAT_HISTORY;
    @Value("classpath:/prompts/final_query_system_prompt.st")
    private Resource finalQueryPrompt;

    public PromptTemplate buildSystemMessage(List<Document> allSegments, String conversationId) {
        log.info("UserMessageBuilderService called");
        String contextBlock = allSegments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        List<Message> messages = chatMemory.get(conversationId);
        int fromIndex = Math.max(0, messages.size() - MAX_CHAT_HISTORY);
        String chatHistory = messages.subList(fromIndex, messages.size()).stream()
                .map(msg -> "Question=%s: Answer=%s".formatted(
                        msg.getMetadata().get("query"),
                        msg.getText()
                ))
                .collect(Collectors.joining("\n\n"));

        return PromptTemplate.builder()
                .resource(finalQueryPrompt)
                .variables(Map.of("available-information", contextBlock, "chat-history", chatHistory))
                .build();
    }
}

