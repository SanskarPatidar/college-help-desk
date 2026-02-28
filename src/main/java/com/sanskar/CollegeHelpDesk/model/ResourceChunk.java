package com.sanskar.CollegeHelpDesk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// This class represent a chunk of any resource(notice, faculty, etc)
// required for metadata to be stored in vector db along with the chunk text and embedding
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceChunk {
    // metadata
    private String id;
    private String header;
    private ResourceType type;
    private String publishedDate;
    private String url;

    // chunk data
    private String chunkText;
    private float[] embedding;
}
