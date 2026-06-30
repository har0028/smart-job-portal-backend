package com.smartjobportal.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterProfileResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String companyName;
    private String companyWebsite;
    private String designation;
    private Integer totalJobs;
}
