package com.smartjobportal.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSkillResponse {
    private Long id;
    private Long skillId;
    private String skillName;
    private String category;
    private Integer proficiencyLevel;
}
