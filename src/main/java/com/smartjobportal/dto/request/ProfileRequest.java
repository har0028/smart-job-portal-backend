package com.smartjobportal.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProfileRequest {

    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    private String phone;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    private String bio;

    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 50, message = "Experience cannot exceed 50 years")
    private Integer yearsExperience;
}
