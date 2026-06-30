package com.smartjobportal.dto.response;

import com.smartjobportal.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Long seekerId;
    private String seekerName;
    private String seekerEmail;
    private ApplicationStatus status;
    private String coverLetter;
    private Double matchScore;
    private LocalDateTime appliedAt;
}
