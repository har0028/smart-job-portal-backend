package com.smartjobportal.ai;

import lombok.*;

import java.util.Set;

/**
 * Intermediate result produced by JaccardScorer.
 * Carries all data needed for ranking and explanation.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchResult {

    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private String jobType;
    private String salaryRange;

    // Raw skill match score 0-100
    private double skillMatchScore;

    // Composite weighted score 0-100
    private double finalScore;

    private Set<String> matchedSkills;
    private Set<String> missingSkills;
    private int totalRequiredSkills;
    private int matchedCount;
}
