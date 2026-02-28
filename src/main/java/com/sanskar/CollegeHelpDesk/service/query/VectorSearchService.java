package com.sanskar.CollegeHelpDesk.service.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.sanskar.CollegeHelpDesk.config.EmbeddingStoreProvider;
import com.sanskar.CollegeHelpDesk.model.ResourceType;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorSearchService {

    private final EmbeddingStoreProvider embeddingStoreProvider;
    private final ElasticsearchClient elasticsearchClient; // spring provides this bean
    @Value("${vector_search.top_k:3}")
    private int topK;
    public List<TextSegment> search(Embedding queryEmbedding, ResourceType type) {
        log.info("VectorSearchService called, searching for top {} matches in type {}", topK, type);
        EmbeddingStore<TextSegment> embeddingStore = embeddingStoreProvider.getStore(type);
        Instant start = Instant.now();
        List<TextSegment> vectorResults = embeddingStore.search(
                        EmbeddingSearchRequest.builder()
                                .queryEmbedding(queryEmbedding)
                                .maxResults(topK)
                                .build()
                )
                .matches()
                .stream()
                .map(EmbeddingMatch::embedded)
                .toList();
        Instant end = Instant.now();
        log.info("Vector search completed in {} ms", end.toEpochMilli() - start.toEpochMilli());
        log.info("Vector search result size: " + vectorResults.size());

//        List<TextSegment> keywordResults = new ArrayList<>();
//        log.info("Running keyword search");
//
//        Instant keyStart = Instant.now();
//        keywordResults = keywordSearch(type, query, topK);
//        Instant keyEnd = Instant.now();
//        log.info("Keyword search completed in {} ms", keyEnd.toEpochMilli() - keyStart.toEpochMilli());
//        log.info("Keyword search result size: " + keywordResults.size());
//
//        return merge(vectorResults, keywordResults);
        return vectorResults;
    }

    private List<TextSegment> keywordSearch(ResourceType type,
                                            String query,
                                            int topK) {
        try {
            String index = type.toString().toLowerCase() + "_index";

            var res = elasticsearchClient.search(s -> s
                            .index(index)
                            .size(topK)
                            .query(q -> q
                                    .match(m -> m
                                            .field("text") // TextSegment = text + metadata
                                            .query(query)
                                    )
                            ),
                    Map.class);

            List<TextSegment> list = new ArrayList<>();

            for (var hit : res.hits().hits()) {
                Map src = hit.source();
                if (src == null) continue;
                String text = (String) src.get("text");
                list.add(TextSegment.from(text));
            }

            return list;

        } catch (Exception e) {
            return List.of();
        }
    }

    private List<TextSegment> merge(List<TextSegment> a, List<TextSegment> b) {
        Map<String, TextSegment> map = new HashMap<>();
        for (TextSegment s : a)
            map.put(s.text(), s);
        for (TextSegment s : b)
            map.put(s.text(), s);
        return new ArrayList<>(map.values());
    }
}
