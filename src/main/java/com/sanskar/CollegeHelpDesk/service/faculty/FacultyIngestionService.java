package com.sanskar.CollegeHelpDesk.service.faculty;

import com.sanskar.CollegeHelpDesk.model.Resource;
import com.sanskar.CollegeHelpDesk.model.ResourceChunk;
import com.sanskar.CollegeHelpDesk.service.ingestion.AbstractIngestionTemplate;
import com.sanskar.CollegeHelpDesk.service.ingestion.EmbeddingService;
import com.sanskar.CollegeHelpDesk.service.ingestion.ResourceSplitter;
import com.sanskar.CollegeHelpDesk.service.ingestion.VectorStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacultyIngestionService extends AbstractIngestionTemplate {
    private final FacultyResourceLoader facultyResourceLoader;
    private final FacultyResourceTransformer facultyResourceTransformer;

    public FacultyIngestionService(
            FacultyResourceLoader facultyResourceLoader,
            FacultyResourceTransformer facultyResourceTransformer,
            ResourceSplitter resourceSplitter,
            EmbeddingService embeddingService,
            VectorStoreService vectorStoreService
    ) {
        super(resourceSplitter, embeddingService, vectorStoreService);
        this.facultyResourceLoader = facultyResourceLoader;
        this.facultyResourceTransformer = facultyResourceTransformer;
    }

    @Override
    protected List<Resource> load(String url) {
        return facultyResourceLoader.load(url);
    }

    @Override
    protected List<String> transform(List<Resource> resources) {
        return facultyResourceTransformer.transform(resources);
    }
}
