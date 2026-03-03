package com.sanskar.CollegeHelpDesk.model;

import lombok.Builder;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Document(indexName = "chat-history")
@Builder
public record ChatMessage(
    @Id
    String id,
    @Field(type = FieldType.Keyword)
    String conversationId,
    @Field(type = FieldType.Keyword)
    String query,
    @Field(type = FieldType.Text)
    String answer,
    @Field(type = FieldType.Date)
    Instant createdAt
) {
    public static Message toMessage(ChatMessage entity) {
        return UserMessage.builder()
                .text(entity.answer())
                .metadata(Map.of(
                        "id", entity.id(),
                        "conversationId", entity.conversationId(),
                        "query", entity.query(),
                        "createdAt", entity.createdAt()
                ))
                .build();
    }

    public static ChatMessage toEntity(String conversationId, Message message) {
        var metadata = message.getMetadata();
        return ChatMessage.builder()
                .id((String) metadata.getOrDefault("id", null))
                .conversationId(conversationId)
                .query((String) metadata.getOrDefault("query", null))
                .answer(message.getText())
                .createdAt((Instant) metadata.getOrDefault("createdAt", Instant.now()))
                .build();
    }

    public static Message queryResponseToMessage(QueryResponse queryResponse) {
        return UserMessage.builder()
                .text(queryResponse.answer())
                .metadata(Map.of(
                        "id", UUID.randomUUID().toString(),
                        "conversationId", queryResponse.conversationId(),
                        "query", queryResponse.query(),
                        "createdAt", Instant.now()
                ))
                .build();
    }
}