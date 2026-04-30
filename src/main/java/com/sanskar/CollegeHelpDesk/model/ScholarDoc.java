package com.sanskar.CollegeHelpDesk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScholarDoc implements Resource{
    private String id;
    private String header;
    private ResourceType type;
    private String publishedDate;
    private String url;
}
