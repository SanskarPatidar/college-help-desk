package com.sanskar.CollegeHelpDesk.service.query;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserMessageBuilderService {
    @Autowired
    private ChatMemory chatMemory;
    @Value("${chat-history.max-messages:5}")
    private int MAX_CHAT_HISTORY;

    public String buildUserMessage(List<Document> allSegments, String query, String conversationId) {
        log.info("UserMessageBuilderService called");
        String contextBlock = allSegments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        List<Message> messages = chatMemory.get(conversationId);
        int fromIndex = Math.max(0, messages.size() - MAX_CHAT_HISTORY);
        String chatHistory = messages.subList(fromIndex, messages.size()).stream()
                .map(msg -> "%s: %s".formatted(
                        msg.getMetadata().get("query"),
                        msg.getText()
                ))
                .collect(Collectors.joining("\n\n"));

        return  """
            ---------------------
            AVAILABLE INFORMATION:
            %s
            
            ---------------------
            CHAT HISTORY:
            %s
            
            ---------------------
            USER QUESTION:
            %s
            """.formatted(contextBlock, chatHistory, query);
    }
}

