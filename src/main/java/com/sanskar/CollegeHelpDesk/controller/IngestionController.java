package com.sanskar.CollegeHelpDesk.controller;

import com.sanskar.CollegeHelpDesk.dto.ScholarDocDTO;
import com.sanskar.CollegeHelpDesk.service.faculty.FacultyIngestionService;
import com.sanskar.CollegeHelpDesk.service.notice.NoticeIngestionService;
import com.sanskar.CollegeHelpDesk.service.scholarship.ScholarDocIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/ingest")
public class IngestionController {
    private final FacultyIngestionService facultyService;
    private final NoticeIngestionService noticeService;
    private final ScholarDocIngestionService scholarDocService;

    @PostMapping("/faculty")
    public String faculty(@RequestParam String url){
        System.out.println("Ingestion URL: " + url);
        facultyService.ingest(url);
        return "Faculty ingestion started";
    }
    // https://iiitbhopal.ac.in/Document/ff/Aashish%20Parihar.html

    @PostMapping("/notices")
    public String notices(@RequestParam String url){
        noticeService.ingest(url);
        return "Notice ingestion started";
    }

    @PostMapping("/scholarship")
    public String scholarship(@RequestBody ScholarDocDTO scholarDocDTO){
        scholarDocService.ingest(scholarDocDTO);
        return "Scholarship document ingestion started";
    }
}
