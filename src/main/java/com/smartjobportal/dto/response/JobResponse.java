package com.smartjobportal.dto.response;

import com.smartjobportal.enums.JobStatus;
import com.smartjobportal.enums.JobType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private JobType jobType;
    private String salaryRange;
    private Integer yearsExperienceRequired;
    private JobStatus status;
    private LocalDateTime postedAt;
    private LocalDateTime expiresAt;
    private String companyName;
    private String recruiterName;
    private Long recruiterId;
    private List<SkillResponse> requiredSkills;
    private List<SkillResponse> optionalSkills;
    private Integer applicationCount;
}
