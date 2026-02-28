package com.sanskar.CollegeHelpDesk.service.faculty;

import com.sanskar.CollegeHelpDesk.model.Faculty;
import com.sanskar.CollegeHelpDesk.model.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Slf4j
public class FacultyResourceTransformer{
    public List<String> transform(List<Resource> resources) {

        Faculty f = (Faculty) resources.getFirst();

        return List.of(
                """
                Name: %s
                Designation: %s
                Email: %s
                Phone: %s

                Qualification:
                %s

                Experience:
                %s

                Teaching:
                %s

                Research Areas:
                %s

                Profile Link:
                %s
                """.formatted(
                        empty(f.getName()),
                        empty(f.getDesignation()),
                        empty(f.getEmail()),
                        empty(f.getPhone()),
                        empty(f.getQualification()),
                        empty(f.getExperience()),
                        empty(f.getTeaching()),
                        empty(f.getAreaOfResearch()),
                        empty(f.getUrl()))
        );
    }

    private String empty(String v){
        return v == null ? "" : v.trim();
    }
}
