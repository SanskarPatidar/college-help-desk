package com.sanskar.CollegeHelpDesk.model;

public interface Resource {
    String getId();
    String getHeader();
    ResourceType getType();
    String getPublishedDate();// optional
    String getUrl();
}
