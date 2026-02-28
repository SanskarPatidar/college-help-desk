package com.sanskar.CollegeHelpDesk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Faculty implements Resource {
    private String id; // common Resource property
    private String name; // common Resource property
    private String designation;
    private String email;
    private String phone;

    private String qualification;
    private String experience;
    private String teaching;
    private String areaOfResearch;

    private ResourceType type; // common Resource property
    private String publishedDate; // common Resource property
    private String url; // common Resource property

    @Override
    public String getHeader() {
        return name;
    }

    @Override
    public ResourceType getType() {
        return type;
    }

    @Override
    public String getPublishedDate() {
        return publishedDate;
    }
}
