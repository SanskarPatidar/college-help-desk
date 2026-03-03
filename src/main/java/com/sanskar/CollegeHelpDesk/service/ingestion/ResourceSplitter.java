package com.sanskar.CollegeHelpDesk.service.ingestion;

import com.sanskar.CollegeHelpDesk.model.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ResourceSplitter {

    private final TextSplitter splitter = new TokenTextSplitter(
            400, // Maximum number of tokens per chunk
            50,   // Minimum characters required to keep a chunk
            50,  // Minimum characters required to keep a chunk
            100, // Maximum chunks generated per document
            true  // Separator to use when splitting text
    ); // overlap is internally managed

    public List<Document> split(List<Resource> resources,
                                List<String> resourceStrings) {

        log.info("Resource splitter called");

        List<Document> documents = new ArrayList<>();

        for (int i = 0; i < resources.size(); i++) {
            documents.addAll(
                    splitSingle(resources.get(i), resourceStrings.get(i))
            );
        }

        return documents;
    }

    private List<Document> splitSingle(Resource resource,
                                       String text) {

        // create base document
        Document base = Document.builder()
                .id(resource.getId())
                .text(text)
                .metadata(Map.of(
                        "header", resource.getHeader(),
                        "type", resource.getType(),
                        "url", resource.getUrl(),
                        "publishedDate", resource.getPublishedDate()
                ))
                .build();

        // split into chunks
        return splitter.split(List.of(base));
    }
}