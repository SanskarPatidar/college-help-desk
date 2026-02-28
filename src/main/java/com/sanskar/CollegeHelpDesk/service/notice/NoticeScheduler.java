package com.sanskar.CollegeHelpDesk.service.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeScheduler {
    private final NoticeIngestionService noticeService;

    @Scheduled(cron="0 0 */6 * * *")
    public void run(){
        noticeService.ingest("https://iiitbhopal.ac.in/api/CMSData/GetNoticeData1?Category=Notice");
    }
}
