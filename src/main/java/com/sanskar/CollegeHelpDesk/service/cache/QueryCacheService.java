package com.sanskar.CollegeHelpDesk.service.cache;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueryCacheService {
    private final ElasticsearchClient elasticsearchClient;
    private final EmbeddingModel embeddingModel;

    @Value("${cache.similarity_threshold:0.85}")
    private float SIMILARITY_THRESHOLD;
    private static final String INDEX = "semantic_cache";

    public String searchSimilar(String query) {
        log.info("QueryCacheService called");

        try {
            float[] vectorArray =
                    embeddingModel.embed(query).content().vector();
            List<Float> vectorList = new ArrayList<>();
            for (float f : vectorArray) {
                vectorList.add(f);
            }

            var response = elasticsearchClient.search(s -> s
                            .index(INDEX)
                            .knn(k -> k
                                    .field("embedding")
                                    .queryVector(vectorList)
                                    .k(1)
                                    .numCandidates(50)
                            ),
                    Map.class);

            if (response.hits().hits().isEmpty())
                return null;

            var hit = response.hits().hits().getFirst();

            double score = hit.score() == null ? 0 : hit.score();

            // cosine similarity approx
            if (score < SIMILARITY_THRESHOLD) {
                log.info("Semantic cache miss (low similarity)");
                return null;
            }

            Map src = hit.source();
            log.info("Semantic cache HIT");

            return (String) src.get("answer");

        } catch (Exception e) {
            log.error("Semantic cache error {}", e.getMessage());
            return null;
        }
    }

    public void store(String query, String answer){

        try{
            float[] vector =
                    embeddingModel.embed(query).content().vector();

            Map<String,Object> doc = Map.of(
                    "query", query,
                    "answer", answer,
                    "embedding", vector,
                    "createdAt", Instant.now().toString()  // for TTL
            );

            elasticsearchClient.index(i -> i
                    .index(INDEX)
                    .document(doc)
            );

        }catch(Exception e){
            log.error("Cache store failed {}", e.getMessage());
        }
    }

}
