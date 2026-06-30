package com.smartjobportal.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillResponse {
    private Long id;
    private String name;
    private String category;
}
