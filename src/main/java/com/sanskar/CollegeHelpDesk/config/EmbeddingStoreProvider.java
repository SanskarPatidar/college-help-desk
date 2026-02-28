package com.sanskar.CollegeHelpDesk.config;

import com.sanskar.CollegeHelpDesk.model.ResourceType;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;

public interface EmbeddingStoreProvider {
    EmbeddingStore<TextSegment> getStore(ResourceType clusterName);
}
