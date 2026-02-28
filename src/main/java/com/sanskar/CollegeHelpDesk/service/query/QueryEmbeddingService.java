package com.sanskar.CollegeHelpDesk.service.query;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueryEmbeddingService {
    private final EmbeddingModel embeddingModel;

    public Embedding embedQuery(String query) {
        log.info("QueryEmbeddingService called");
        Instant start = Instant.now();
        Embedding embedding = embeddingModel.embed(query).content();
        Instant end = Instant.now();
        log.info("Embedding generated in {} ms", end.toEpochMilli() - start.toEpochMilli());
        return embedding;
    }
}
