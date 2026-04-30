package com.sanskar.CollegeHelpDesk.service.scholarship;

import com.sanskar.CollegeHelpDesk.model.Notice;
import com.sanskar.CollegeHelpDesk.model.Resource;
import com.sanskar.CollegeHelpDesk.model.ScholarDoc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ScholarDocResourceTransformer {
    protected List<String> transform(List<Resource> resources) {
        log.info("Resource Transformation called for ScholarDoc");
        List<String> scholarDocStrings = new ArrayList<>();
        for (Resource r : resources) {
            ScholarDoc scholarDoc = (ScholarDoc) r;
            String string = transformToString(scholarDoc);
            scholarDocStrings.add(string);
        }
        return scholarDocStrings;
    }
    private String transformToString(ScholarDoc scholarDoc) {
        // Clean null fields safely
        String title = empty(scholarDoc.getHeader());
        String date = empty(scholarDoc.getPublishedDate());
        String noticeId = empty(scholarDoc.getId());
        String link = empty(scholarDoc.getUrl());

        return """
                Title: %s
                Published: %s
                ScholarDocId: %s

                Document Link:
                %s
                """.formatted(title, date, noticeId, link);
    }
    private String empty(String val) {
        return val == null ? "" : val.trim();
    }
}
