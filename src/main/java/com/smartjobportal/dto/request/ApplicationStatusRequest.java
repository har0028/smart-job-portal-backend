package com.smartjobportal.dto.request;

import com.smartjobportal.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationStatusRequest {

    @NotNull(message = "Status is required")
    private ApplicationStatus status;
}
