package com.sanskar.CollegeHelpDesk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScholarDocDTO {
    private String header;
    private String url;
}
