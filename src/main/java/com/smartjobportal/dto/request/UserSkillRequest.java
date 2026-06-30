package com.smartjobportal.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserSkillRequest {

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @Min(value = 1, message = "Proficiency level must be at least 1")
    @Max(value = 5, message = "Proficiency level cannot exceed 5")
    private Integer proficiencyLevel = 1;
}
