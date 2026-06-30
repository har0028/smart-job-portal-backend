package com.smartjobportal.dto.response;

import com.smartjobportal.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
