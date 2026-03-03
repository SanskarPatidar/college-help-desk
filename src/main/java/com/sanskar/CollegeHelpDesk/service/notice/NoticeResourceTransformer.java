package com.sanskar.CollegeHelpDesk.service.notice;

import com.sanskar.CollegeHelpDesk.model.Notice;
import com.sanskar.CollegeHelpDesk.model.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class NoticeResourceTransformer {

    public List<String> transform(List<Resource> resources) {
        log.info("Resource Transformer called for list of notices");
        List<String> noticeStrings = new ArrayList<>();

        for (Resource n : resources) {
            Notice notice = (Notice)n;
            String noticeString = transformNoticeToString(notice);
            noticeStrings.add(noticeString);
        }

        return noticeStrings; // ready for splitting
    }

    public String transformNoticeToString(Notice notice) {
        // Clean null fields safely
        String title = empty(notice.getTitle());
        String date = empty(notice.getPublishedDate());
        String noticeId = empty(notice.getId());
        String content = empty(notice.getNoticeData());
        String link = empty(notice.getUrl());

        return """
                Title: %s
                Published: %s
                NoticeId: %s

                Content:
                %s

                Document Link:
                %s
                """.formatted(title, date, noticeId, content, link);
    }

    private String empty(String val) {
        return val == null ? "" : val.trim();
    }
}

