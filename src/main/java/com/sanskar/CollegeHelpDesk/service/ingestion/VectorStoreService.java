package com.sanskar.CollegeHelpDesk.service.ingestion;

import com.sanskar.CollegeHelpDesk.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorStoreService {
    @Autowired
    private Map<String, VectorStore> embeddingStores;

    public void storeAll(List<Document> docs) {
        log.info("Vector storage service called");
        System.out.println("Storing " + docs.size() + " chunks in vector store");
        for (Document doc : docs) {
            ResourceType type = (ResourceType) doc.getMetadata().get("type");
            embeddingStores.get(resolveIndex(type)).add(List.of(doc));
        }
    }
    private String resolveIndex(ResourceType type) {
        return switch (type) {
            case ResourceType.FACULTY -> "faculty-index";
            case ResourceType.NOTICE -> "syllabus-index";
            default -> "notice-index";
        };
    }
}


