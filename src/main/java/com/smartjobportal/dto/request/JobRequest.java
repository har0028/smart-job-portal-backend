package com.smartjobportal.dto.request;

import com.smartjobportal.enums.JobStatus;
import com.smartjobportal.enums.JobType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobRequest {

    @NotBlank(message = "Job title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    private JobType jobType = JobType.FULL_TIME;

    @Size(max = 50, message = "Salary range cannot exceed 50 characters")
    private String salaryRange;

    @Min(value = 0, message = "Experience cannot be negative")
    private Integer yearsExperienceRequired = 0;

    private JobStatus status = JobStatus.ACTIVE;

    private LocalDateTime expiresAt;

    @NotEmpty(message = "At least one skill is required")
    private List<Long> skillIds;

    private List<Long> optionalSkillIds;
}
