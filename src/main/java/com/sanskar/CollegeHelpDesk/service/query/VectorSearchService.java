package com.sanskar.CollegeHelpDesk.service.query;

import com.sanskar.CollegeHelpDesk.model.ResourceType;
import com.sanskar.CollegeHelpDesk.repository.VectorSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorSearchService {
    private final VectorSearchRepository vectorSearchRepository;

    public List<Document> search(String query, ResourceType type) {
        log.info("VectorSearchService called, searching  in type {}", type);
        Instant start = Instant.now();
        List<Document> vectorResults = vectorSearchRepository.searchByEmbeddingAndType(query, type);
        Instant end = Instant.now();
        log.info("Vector search completed in {} ms", end.toEpochMilli() - start.toEpochMilli());
        log.info("Vector search result size: " + vectorResults.size());
        return vectorResults;
    }


}
