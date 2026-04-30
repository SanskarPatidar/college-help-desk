package com.sanskar.CollegeHelpDesk.service.scholarship;

import com.sanskar.CollegeHelpDesk.dto.ScholarDocDTO;
import com.sanskar.CollegeHelpDesk.model.Resource;
import com.sanskar.CollegeHelpDesk.model.ResourceType;
import com.sanskar.CollegeHelpDesk.model.ScholarDoc;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ScholarDocResourceLoader {
    public List<Resource> load(ScholarDocDTO dto) {
        return List.of(ScholarDoc.builder()
                .id(UUID.randomUUID().toString())
                .url(dto.getUrl())
                .header(dto.getHeader())
                .publishedDate(LocalDateTime.now().toString())
                .type(ResourceType.SCHOLARDOC)
                .build()
        );
    }
}
