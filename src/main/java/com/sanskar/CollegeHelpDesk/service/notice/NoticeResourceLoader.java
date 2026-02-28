package com.sanskar.CollegeHelpDesk.service.notice;

import com.sanskar.CollegeHelpDesk.model.Notice;
import com.sanskar.CollegeHelpDesk.model.Resource;
import com.sanskar.CollegeHelpDesk.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NoticeResourceLoader {
    private final RestTemplate restTemplate;
    private LocalDate lastLoadedDate = LocalDate.MIN;

    public List<Resource> load(String url) {
        // "https://iiitbhopal.ac.in/api/CMSData/GetNoticeData1?Category=Notice"
        log.info("Resource loader called");
        try {
            Notice[] notices = restTemplate.getForObject(
                    url,
                    Notice[].class
            );
            log.info("Api called");
            if(notices == null) throw new Exception("Received initial null response from notices Api");
            List<Resource> noticeList = new ArrayList<>();
            for(Notice notice : notices) {
                String[] data = notice.getNoticeData().split(";");
                String dateString = notice.getPublishedDate().substring(0, 10);
                if(LocalDate.parse(dateString).isBefore(lastLoadedDate))continue; // filter already loaded notices
                if(data.length % 4 != 0) throw new Exception("Invalid data received from notices API");
                for(int i = 0; i < data.length; i+=4){
                    notice.setId(UUID.randomUUID().toString());
                    notice.setNoticeData(data[i]);
                    notice.setUrl(notice.getUrl() + "/" + data[i+2] + ".pdf");
                    notice.setPublishedDate(dateString);
                    noticeList.add(notice);
                    notice.setType(ResourceType.NOTICE);
                }
            }
            lastLoadedDate = LocalDate.now();
            return noticeList;
        } catch (Exception e) {
            log.error("Resource loading error: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
