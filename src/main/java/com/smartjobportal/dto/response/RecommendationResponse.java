package com.smartjobportal.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponse {
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private String jobType;
    private String salaryRange;
    private Double matchPercentage;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String recommendationReason;
    private Integer rank;
}
