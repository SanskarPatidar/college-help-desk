package com.sanskar.CollegeHelpDesk.service.ingestion;

import com.sanskar.CollegeHelpDesk.model.Resource;
import com.sanskar.CollegeHelpDesk.model.ResourceChunk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ResourceSplitter {
    private final SimpleTextSplitter splitter = new SimpleTextSplitter(400, 50); // size, overlap

    public List<ResourceChunk> split(List<Resource> resources, List<String> resourceStrings) {
        log.info("Resource splitter called");
        List<ResourceChunk> chunks = new ArrayList<>();
        for (int i = 0; i < resources.size(); i++) {
            chunks.addAll(splitSingleChunk(resources.get(i), resourceStrings.get(i)));
        }
        return chunks;
    }

    public List<ResourceChunk> splitSingleChunk(Resource resource, String resourceString) {
        List<String> segments = splitter.split(resourceString);
        List<ResourceChunk> chunks = new ArrayList<>();
        for (String seg : segments) {
            chunks.add(ResourceChunk.builder()
                    .id(resource.getId())
                    .header(resource.getHeader())
                    .type(resource.getType())
                    .publishedDate(resource.getPublishedDate())
                    .url(resource.getUrl())
                    .chunkText(seg)
                    .build() // keep embedding as null for now
            );
        }
        return chunks;
    }
}
