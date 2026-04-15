package com.sanskar.CollegeHelpDesk.service.notice;

import com.sanskar.CollegeHelpDesk.model.Notice;
import com.sanskar.CollegeHelpDesk.model.Resource;
import com.sanskar.CollegeHelpDesk.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class NoticeResourceLoader {
    private final RestTemplate restTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public List<Resource> load(String url) {
        // "https://iiitbhopal.ac.in/api/CMSData/GetNoticeData1?Category=Notice"
        log.info("Resource loader called");

        String value = redisTemplate.opsForValue().get("NOTICE_LAST_LOAD_DATE");
        LocalDate lastLoadedDate = value != null ? LocalDate.parse(value) : LocalDate.MIN;

        try {
            Notice[] notices = restTemplate.getForObject(
                    url,
                    Notice[].class
            );
            Arrays.stream(notices).forEach(notice -> System.out.println(notice));
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
            redisTemplate.opsForValue().set("NOTICE_LAST_LOAD_DATE", LocalDate.now().toString()); // no TTL
            return noticeList;
        } catch (Exception e) {
            log.error("Resource loading error: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
