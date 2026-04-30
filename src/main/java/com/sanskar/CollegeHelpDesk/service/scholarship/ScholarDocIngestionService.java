package com.sanskar.CollegeHelpDesk.service.scholarship;

import com.sanskar.CollegeHelpDesk.dto.ScholarDocDTO;
import com.sanskar.CollegeHelpDesk.model.Resource;
import com.sanskar.CollegeHelpDesk.service.ingestion.AbstractIngestionTemplate;
import com.sanskar.CollegeHelpDesk.service.ingestion.ResourceSplitter;
import com.sanskar.CollegeHelpDesk.service.ingestion.VectorStoreService;
import com.sanskar.CollegeHelpDesk.service.notice.NoticeResourceLoader;
import com.sanskar.CollegeHelpDesk.service.notice.NoticeResourceTransformer;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScholarDocIngestionService extends AbstractIngestionTemplate<ScholarDocDTO> { // dto as input
    private final ScholarDocResourceLoader scholarDocResourceLoader;
    private final ScholarDocResourceTransformer scholarDocResourceTransformer;

    public ScholarDocIngestionService(
            ScholarDocResourceLoader scholarDocResourceLoader,
            ScholarDocResourceTransformer scholarDocResourceTransformer,
            ResourceSplitter resourceSplitter,
            VectorStoreService vectorStoreService) {
        super(resourceSplitter, vectorStoreService);
        this.scholarDocResourceLoader = scholarDocResourceLoader;
        this.scholarDocResourceTransformer = scholarDocResourceTransformer;
    }

    @Override
    protected List<Resource> load(ScholarDocDTO scholarDocDTO) {
        return scholarDocResourceLoader.load(scholarDocDTO);
    }

    @Override
    protected List<String> transform(List<Resource> resources) {
        return scholarDocResourceTransformer.transform(resources);
    }
}
