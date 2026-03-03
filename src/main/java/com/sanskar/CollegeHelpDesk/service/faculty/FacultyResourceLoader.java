package com.sanskar.CollegeHelpDesk.service.faculty;

import com.sanskar.CollegeHelpDesk.model.Faculty;
import com.sanskar.CollegeHelpDesk.model.Resource;
import com.sanskar.CollegeHelpDesk.model.ResourceType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
class FacultyResourceLoader {
    public List<Resource> load(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            // get section with id and get internal elements by their name

            // basic info
            String name = text(doc.selectFirst("#bio h1"));
            String designation = text(doc.selectFirst("#bio h5"));

            // phone & email
            String phone = extractAfterLabel(doc, "Phone:");
            String email = extractAfterLabel(doc, "Email:");

            // ===== SECTIONS =====
            String qualification = extractSection(doc, "educationDescription");
            String experience = extractSection(doc, "experienceDis");
            String teaching = extractSection(doc, "teachingDetails");
            String researchAreas = extractSection(doc, "areaOfresearchDis");

            return List.of(Faculty.builder()
                    .id(UUID.randomUUID().toString())
                    .name(name)
                    .designation(designation)
                    .phone(phone)
                    .email(email)
                    .qualification(qualification)
                    .experience(experience)
                    .teaching(teaching)
                    .areaOfResearch(researchAreas)
                    .type(ResourceType.FACULTY)
                    .publishedDate(LocalDateTime.now().toString())
                    .url(url)
                    .build());

        } catch (Exception e) {
            throw new RuntimeException("Faculty parsing failed: " + e.getMessage(), e);
        }
    }

    private String text(Element el) {
        return el == null ? "" : el.text().trim();
    }

    private String extractAfterLabel(Document doc, String label) {

        // get all h5 in section with id bio
        for (Element h5 : doc.select("#bio h5")) {
            String txt = h5.text();
            // any h5 with given label
            if (txt.contains(label)) {
                return txt.replace(label, "").trim(); // remove label, return rest
            }
        }
        return "";
    }
    private String extractSection(Document doc, String id) {

        StringBuilder sb = new StringBuilder();
        // get li items in the section with given id
        Elements items = doc.select("#" + id + " li");
        for (Element li : items) {
            sb.append(li.text().trim());
            if (items.last() != li) sb.append(", ");
        }
        return sb.toString();
    }
}