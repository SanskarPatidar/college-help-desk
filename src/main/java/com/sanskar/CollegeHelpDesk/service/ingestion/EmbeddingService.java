package com.sanskar.CollegeHelpDesk.service.ingestion;

import com.sanskar.CollegeHelpDesk.model.ResourceChunk;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {
    private final EmbeddingModel embeddingModel; // more generic than OllamaEmbeddingModel

    public void embedAll(List<ResourceChunk> chunks) {
        log.info("Embedding service called");
        for (ResourceChunk chunk : chunks) {
            Embedding embedding = embeddingModel
                    .embed(chunk.getChunkText())
                    .content();

            chunk.setEmbedding(embedding.vector());
        }
    }
}

