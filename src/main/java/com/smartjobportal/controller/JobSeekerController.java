package com.smartjobportal.controller;

import com.smartjobportal.dto.request.ApplicationRequest;
import com.smartjobportal.dto.request.ProfileRequest;
import com.smartjobportal.dto.request.UserSkillRequest;
import com.smartjobportal.dto.response.*;
import com.smartjobportal.service.ApplicationService;
import com.smartjobportal.service.SavedJobService;
import com.smartjobportal.service.SeekerService;
import com.smartjobportal.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/seeker")
@RequiredArgsConstructor
@PreAuthorize("hasRole('JOB_SEEKER')")
public class JobSeekerController {

    private final SeekerService seekerService;
    private final ApplicationService applicationService;
    private final SavedJobService savedJobService;
    private final SecurityUtils securityUtils;

    // ── Profile ───────────────────────────────────────────────────────────────

    /**
     * GET /api/seeker/profile
     * Response: seeker profile with skills
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<SeekerProfileResponse>> getProfile() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.success("Profile retrieved", seekerService.getProfile(userId)));
    }

    /**
     * PUT /api/seeker/profile
     * Body: ProfileRequest
     * Response: updated SeekerProfileResponse
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<SeekerProfileResponse>> updateProfile(
            @Valid @RequestBody ProfileRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        SeekerProfileResponse profile = seekerService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
    }

    // ── Resume ────────────────────────────────────────────────────────────────

    /**
     * POST /api/seeker/resume  (multipart/form-data, field: file)
     * Response: resume URL string
     */
    @PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadResume(
            @RequestParam("file") MultipartFile file) {
        Long userId = securityUtils.getCurrentUserId();
        String resumeUrl = seekerService.uploadResume(userId, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resume uploaded successfully", resumeUrl));
    }

    // ── Skills ────────────────────────────────────────────────────────────────

    /**
     * GET /api/seeker/skills
     * Response: list of user's skills with proficiency levels
     */
    @GetMapping("/skills")
    public ResponseEntity<ApiResponse<List<UserSkillResponse>>> getMySkills() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.success("My skills", seekerService.getMySkills(userId)));
    }

    /**
     * POST /api/seeker/skills
     * Body: { skillId, proficiencyLevel }
     * Response: 201 + UserSkillResponse
     */
    @PostMapping("/skills")
    public ResponseEntity<ApiResponse<UserSkillResponse>> addSkill(
            @Valid @RequestBody UserSkillRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        UserSkillResponse skill = seekerService.addSkill(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Skill added successfully", skill));
    }

    /**
     * DELETE /api/seeker/skills/{skillId}
     * Response: 200 success message
     */
    @DeleteMapping("/skills/{skillId}")
    public ResponseEntity<ApiResponse<Void>> removeSkill(@PathVariable Long skillId) {
        Long userId = securityUtils.getCurrentUserId();
        seekerService.removeSkill(userId, skillId);
        return ResponseEntity.ok(ApiResponse.success("Skill removed successfully"));
    }

    // ── Applications ──────────────────────────────────────────────────────────

    /**
     * POST /api/seeker/jobs/{jobId}/apply
     * Body: { coverLetter }
     * Response: 201 + ApplicationResponse
     */
    @PostMapping("/jobs/{jobId}/apply")
    public ResponseEntity<ApiResponse<ApplicationResponse>> applyForJob(
            @PathVariable Long jobId,
            @RequestBody ApplicationRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        ApplicationResponse application = applicationService.applyForJob(jobId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Application submitted successfully", application));
    }

    /**
     * GET /api/seeker/applications
     * Response: list of all applications by the seeker
     */
    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getMyApplications() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.success("My applications", applicationService.getMyApplications(userId)));
    }

    /**
     * DELETE /api/seeker/applications/{id}
     * Response: 200 success message
     */
    @DeleteMapping("/applications/{id}")
    public ResponseEntity<ApiResponse<Void>> withdrawApplication(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        applicationService.withdrawApplication(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Application withdrawn"));
    }

    // ── Saved Jobs ────────────────────────────────────────────────────────────

    /**
     * POST /api/seeker/jobs/{jobId}/save
     * Response: 201 success message
     */
    @PostMapping("/jobs/{jobId}/save")
    public ResponseEntity<ApiResponse<Void>> saveJob(@PathVariable Long jobId) {
        Long userId = securityUtils.getCurrentUserId();
        savedJobService.saveJob(jobId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job saved successfully"));
    }

    /**
     * DELETE /api/seeker/jobs/{jobId}/save
     * Response: 200 success message
     */
    @DeleteMapping("/jobs/{jobId}/save")
    public ResponseEntity<ApiResponse<Void>> unsaveJob(@PathVariable Long jobId) {
        Long userId = securityUtils.getCurrentUserId();
        savedJobService.unsaveJob(jobId, userId);
        return ResponseEntity.ok(ApiResponse.success("Job removed from saved list"));
    }

    /**
     * GET /api/seeker/saved-jobs
     * Response: list of saved JobResponse objects
     */
    @GetMapping("/saved-jobs")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getSavedJobs() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.success("Saved jobs", savedJobService.getMySavedJobs(userId)));
    }
}
