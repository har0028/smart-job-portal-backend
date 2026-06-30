package com.smartjobportal.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SkillRequest {

    @NotBlank(message = "Skill name is required")
    @Size(max = 100, message = "Skill name cannot exceed 100 characters")
    private String name;

    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;
}
