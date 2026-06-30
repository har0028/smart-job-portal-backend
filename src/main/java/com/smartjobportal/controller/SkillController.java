package com.smartjobportal.controller;

import com.smartjobportal.dto.request.SkillRequest;
import com.smartjobportal.dto.response.ApiResponse;
import com.smartjobportal.dto.response.SkillResponse;
import com.smartjobportal.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    /**
     * GET /api/skills?keyword=java
     * PUBLIC - returns all skills or filtered by keyword
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getAllSkills(
            @RequestParam(required = false) String keyword) {
        List<SkillResponse> skills = (keyword != null && !keyword.isBlank())
                ? skillService.searchSkills(keyword)
                : skillService.getAllSkills();
        return ResponseEntity.ok(ApiResponse.success("Skills retrieved", skills));
    }

    /**
     * POST /api/skills
     * ADMIN only - creates a new skill
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SkillResponse>> createSkill(
            @Valid @RequestBody SkillRequest request) {
        SkillResponse skill = skillService.createSkill(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Skill created successfully", skill));
    }
}
