package com.sanskar.CollegeHelpDesk.repository;

import com.sanskar.CollegeHelpDesk.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Repository;

import java.util.List;

// Just a layer on top of elasticsearch repo
// Can also directly use elasticsearch repo
// But want to be using any advisor which requires ChatMemory(which takes a ChatMemoryRepository like this)
@Repository
@RequiredArgsConstructor
public class ChatHistoryRepositoryImpl implements ChatMemoryRepository {
    private final ChatHistoryElasticsearchRepository repository;
    @Override
    public List<String> findConversationIds() {
        return repository.findAll()
                .stream()
                .map(ChatMessage::conversationId)
                .distinct()
                .toList();
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        return repository.findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(ChatMessage::toMessage)
                .toList();
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        List<ChatMessage> docs = messages.stream()
                .map(msg -> ChatMessage.toEntity(conversationId, msg))
                .toList();

        repository.saveAll(docs);
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        repository.deleteByConversationId(conversationId);
    }


}