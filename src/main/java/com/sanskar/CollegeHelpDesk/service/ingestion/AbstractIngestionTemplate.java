package com.sanskar.CollegeHelpDesk.service.ingestion;

import com.sanskar.CollegeHelpDesk.model.Resource;
import com.sanskar.CollegeHelpDesk.model.ResourceChunk;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractIngestionTemplate {
    private final ResourceSplitter resourceSplitter;
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;

    public final void ingest(String url) {
        log.info("Ingestion started");
        List<Resource> resources = load(url);
        if(resources == null || resources.isEmpty()){
            log.info("No resources found");
            return;
        }
        List<String> texts = transform(resources);
        List<ResourceChunk> chunks = split(resources, texts);
        embed(chunks);
        store(chunks);
        log.info("Ingestion completed");
    }

    protected abstract List<Resource> load(String url);
    protected abstract List<String> transform(List<Resource> resources);
    protected List<ResourceChunk> split(List<Resource> resources, List<String> texts) {
        return resourceSplitter.split(resources, texts);
    };
    protected void embed(List<ResourceChunk> chunks) {
        embeddingService.embedAll(chunks);
    };
    protected void store(List<ResourceChunk> chunks) {
        vectorStoreService.storeAll(chunks);
    };
}

