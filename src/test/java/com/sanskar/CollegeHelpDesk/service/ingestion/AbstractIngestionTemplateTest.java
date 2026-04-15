package com.sanskar.CollegeHelpDesk.service.ingestion;

import com.sanskar.CollegeHelpDesk.service.notice.NoticeIngestionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AbstractIngestionTemplateTest {
    @Autowired
    @Qualifier("noticeIngestionService")
    private NoticeIngestionService noticeService;

    @Test
    void ingest() {
        noticeService.ingest("https://iiitbhopal.ac.in/api/CMSData/GetNoticeData1?Category=Notice");
        Assertions.assertNull(null);
    }
}