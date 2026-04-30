package com.sanskar.CollegeHelpDesk.service.ingestion;

import com.sanskar.CollegeHelpDesk.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractIngestionTemplate<T> {
    private final ResourceSplitter resourceSplitter;
    private final VectorStoreService vectorStoreService;

    public final void ingest(T input) {
        log.info("Ingestion started");
        List<Resource> resources = load(input);
        if(resources == null || resources.isEmpty()){
            log.info("No resources found");
            return;
        }
        List<String> texts = transform(resources);
        List<Document> docs = split(resources, texts);
        store(docs);
        log.info("Ingestion completed");
    }

    protected abstract List<Resource> load(T input);
    protected abstract List<String> transform(List<Resource> resources);
    protected List<Document> split(List<Resource> resources, List<String> texts) {
        return resourceSplitter.split(resources, texts);
    }
    protected void store(List<Document> docs) {
        vectorStoreService.storeAll(docs);
    }
}

