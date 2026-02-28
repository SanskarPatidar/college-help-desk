package com.sanskar.CollegeHelpDesk.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.sanskar.CollegeHelpDesk.model.ResourceType;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Primary // Marks this as the primary implementation of EmbeddingStoreProvider for dependency injection
public class ElasticsearchStoreProvider implements EmbeddingStoreProvider {
    /**
    * Spring has in-built caching using annotations, but this is just for prototyping
    * In-memory cache can be switched to Redis
    */
    private final Map<String, EmbeddingStore<TextSegment>> cache = new ConcurrentHashMap<>();
    private final RestClient restClient;

    public synchronized EmbeddingStore<TextSegment> getStore(ResourceType type) {

        String indexName = resolveIndex(type.toString().toLowerCase());

        if (cache.containsKey(indexName)) {
            return cache.get(indexName);
        }

        EmbeddingStore<TextSegment> store =
                ElasticsearchEmbeddingStore.builder()
                        .restClient(restClient)
                        .indexName(indexName)
                        .build();

        cache.put(indexName, store);
        System.out.println("Created cache for elasticsearch store: " + cache);
        return store;
    }

    private String resolveIndex(String type) {
        return switch (type.toLowerCase()) {
            case "faculty" -> "faculty_index";
            case "syllabus" -> "syllabus_index";
            case "events" -> "events_index";
            default -> "notices_index";
        };
    }

}
