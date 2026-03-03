package com.sanskar.CollegeHelpDesk.repository;

import com.sanskar.CollegeHelpDesk.model.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class VectorSearchRepository {
    private final Map<String, VectorStore> vectorStoreMap;
    @Value("${vector-database.top-k:3}")
    private int TOP_K;
    @Value("${vector-database.similarity-threshold:0.8}")
    private double similarityThreshold;

    public List<Document> searchByEmbeddingAndType(String query, ResourceType type) {
        VectorStore vectorStore = vectorStoreMap.get(resolveIndex(type));
        return vectorStore
                .similaritySearch(SearchRequest.builder()
                        .topK(TOP_K)
                        .query(query)
                        .similarityThreshold(similarityThreshold)
                        .build()
                );
    }

    private String resolveIndex(ResourceType type) {
        return switch (type) {
            case ResourceType.FACULTY -> "faculty-index";
            case ResourceType.NOTICE -> "notice-index";
        };
    }
}
