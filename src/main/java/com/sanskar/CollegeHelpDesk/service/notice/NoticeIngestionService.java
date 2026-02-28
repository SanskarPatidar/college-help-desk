package com.sanskar.CollegeHelpDesk.service.notice;

import com.sanskar.CollegeHelpDesk.model.Resource;
import com.sanskar.CollegeHelpDesk.service.ingestion.AbstractIngestionTemplate;
import com.sanskar.CollegeHelpDesk.service.ingestion.EmbeddingService;
import com.sanskar.CollegeHelpDesk.service.ingestion.ResourceSplitter;
import com.sanskar.CollegeHelpDesk.service.ingestion.VectorStoreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeIngestionService extends AbstractIngestionTemplate {
    private final NoticeResourceLoader noticeResourceLoader;
    private final NoticeResourceTransformer noticeResourceTransformer;

    public NoticeIngestionService(
            NoticeResourceLoader noticeResourceLoader,
            NoticeResourceTransformer noticeResourceTransformer,
            ResourceSplitter resourceSplitter,
            EmbeddingService embeddingService,
            VectorStoreService vectorStoreService
    ) {
        super(resourceSplitter, embeddingService, vectorStoreService);
        this.noticeResourceLoader = noticeResourceLoader;
        this.noticeResourceTransformer = noticeResourceTransformer;
    }

    @Override
    protected List<Resource> load(String url) {
        return noticeResourceLoader.load(url);
    }

    @Override
    protected List<String> transform(List<Resource> resources) {
        return noticeResourceTransformer.transform(resources);
    }
}
