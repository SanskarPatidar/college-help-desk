package com.sanskar.CollegeHelpDesk.service.ingestion;

import com.sanskar.CollegeHelpDesk.config.EmbeddingStoreProvider;
import com.sanskar.CollegeHelpDesk.model.ResourceChunk;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorStoreService {
    private final EmbeddingStoreProvider embeddingStoreProvider;

    public void storeAll(List<ResourceChunk> chunks) {
        log.info("Vector storage service called");
        System.out.println("Storing " + chunks.size() + " chunks in vector store");
        for (ResourceChunk chunk : chunks) {
            // metadata for retrieval
            Map<String, String> map = Map.of(
                    "resourceId", chunk.getId(),
                    "header", chunk.getHeader(),
                    "type", chunk.getType().toString(),
                    "publishedDate", chunk.getPublishedDate(),
                    "url", chunk.getUrl()
            );
            Metadata metadata = new Metadata(map);

            // TextSegment = text + metadata
            // text to be stored along with embeddings for retrieval
            // metadata to be stored for better query results
            TextSegment segment = TextSegment.from(
                    chunk.getChunkText(),
                    metadata
            );

            Embedding emb = new Embedding(chunk.getEmbedding());

            // Store TextSegment + embedding
            EmbeddingStore<TextSegment> embeddingStore = embeddingStoreProvider.getStore(chunk.getType());
            embeddingStore.add(
                    emb,
                    segment
            );
        }
    }
}


