package com.smartjobportal.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeekerProfileResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String location;
    private String bio;
    private String resumeUrl;
    private Integer yearsExperience;
    private List<UserSkillResponse> skills;
}
