package com.sanskar.CollegeHelpDesk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notice implements Resource {

    @JsonProperty("NoticeId") private String id; // common Resource property
    @JsonProperty("Title") private String title; // common Resource property

    @JsonProperty("NoticeData") private String noticeData;
    @JsonProperty("FileData") private String url; // common Resource property
    @JsonProperty("NoticeDetails") private String noticeDetails;

    private ResourceType type; // common Resource property
    @JsonProperty("PublishedDate") private String publishedDate; // common Resource property

    @Override
    public String getHeader() {
        return title;
    }

    @Override
    public ResourceType getType() {
        return type;
    }

}

