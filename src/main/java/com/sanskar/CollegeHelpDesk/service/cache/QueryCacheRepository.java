package com.sanskar.CollegeHelpDesk.service.cache;

import com.sanskar.CollegeHelpDesk.model.QueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@Slf4j
public class QueryCacheRepository {

    @Value("${vector-cache.similarity_threshold:0.8}")
    private float SIMILARITY_THRESHOLD;
    @Autowired
    @Qualifier("cache-index")
    private VectorStore vectorStore;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public QueryResponse searchSimilar(String query, String conversationId) {
        long start = System.currentTimeMillis();
        log.info("QueryCacheRepository called");
        List<Document> similarResults = vectorStore.similaritySearch(SearchRequest.builder()
                        .query(query)
                        .similarityThreshold(SIMILARITY_THRESHOLD)
                        .topK(1)
                .build());
        if(similarResults.isEmpty()) {
            log.info("No similar query found in cache");
            return null;
        }
        Document mostSimilar = similarResults.getFirst();
        log.info("Found similar query in cache with similarity score: {}", mostSimilar.getScore());
        String answer = redisTemplate.opsForValue().get(mostSimilar.getId());
        if (answer == null) {
            log.info("Cache expired for id: {}, deleting from vector store", mostSimilar.getId());
            vectorStore.delete(List.of(mostSimilar.getId()));
            return null;
        }
        log.info("Similarity Cache search took: {} ms", System.currentTimeMillis() - start);
        return QueryResponse.builder()
                .conversationId(conversationId)
                .query(query)
                .answer(answer)
                .totalTokens(0)
                .latency(System.currentTimeMillis() - start)
                .cached(true)
                .build();
    }

    public void store(String query, String answer) {
        Document doc = Document.builder()
                .id(UUID.randomUUID().toString())
                .text(query) // based on what should we do the similarity search
                .build();
        redisTemplate.opsForValue().set(doc.getId(), answer, Duration.ofHours(6)); // TTL = 6 hrs
        vectorStore.add(List.of(doc));
    }
}
