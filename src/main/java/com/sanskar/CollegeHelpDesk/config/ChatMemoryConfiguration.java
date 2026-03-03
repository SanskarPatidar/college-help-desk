package com.sanskar.CollegeHelpDesk.config;

import com.sanskar.CollegeHelpDesk.repository.ChatHistoryRepositoryImpl;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfiguration {
    @Autowired
    private ChatHistoryRepositoryImpl chatHistoryRepository;

    @Bean
    ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .chatMemoryRepository(chatHistoryRepository)
                .build();
    }
}
