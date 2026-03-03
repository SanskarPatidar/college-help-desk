package com.sanskar.CollegeHelpDesk.repository;

import com.sanskar.CollegeHelpDesk.model.ChatMessage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
// Elasticsearch data and Elasticsearch vector in same project
@Repository
public interface ChatHistoryElasticsearchRepository extends ElasticsearchRepository<ChatMessage, String> {
    List<ChatMessage> findAll();
    List<ChatMessage> findByConversationIdOrderByCreatedAtAsc(String conversationId);
    void deleteByConversationId(String conversationId);
}