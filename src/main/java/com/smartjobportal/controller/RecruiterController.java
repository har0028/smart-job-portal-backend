package com.smartjobportal.controller;

import com.smartjobportal.dto.request.ApplicationStatusRequest;
import com.smartjobportal.dto.request.JobRequest;
import com.smartjobportal.dto.response.*;
import com.smartjobportal.service.ApplicationService;
import com.smartjobportal.service.JobService;
import com.smartjobportal.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterController {

    private final JobService jobService;
    private final ApplicationService applicationService;
    private final SecurityUtils securityUtils;

    /**
     * GET /api/recruiter/jobs
     * Response: all jobs posted by the authenticated recruiter
     */
    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getMyJobs() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.success("My job listings", jobService.getRecruiterJobs(userId)));
    }

    /**
     * POST /api/recruiter/jobs
     * Body: JobRequest
     * Response: 201 + created JobResponse
     */
    @PostMapping("/jobs")
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @Valid @RequestBody JobRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        JobResponse job = jobService.createJob(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job created successfully", job));
    }

    /**
     * PUT /api/recruiter/jobs/{id}
     * Body: JobRequest
     * Response: 200 + updated JobResponse
     */
    @PutMapping("/jobs/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        JobResponse job = jobService.updateJob(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Job updated successfully", job));
    }

    /**
     * DELETE /api/recruiter/jobs/{id}
     * Response: 200 success message
     */
    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        jobService.deleteJob(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Job deleted successfully"));
    }

    /**
     * GET /api/recruiter/jobs/{id}/applicants
     * Response: list of applications for the given job
     */
    @GetMapping("/jobs/{id}/applicants")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplicants(
            @PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        List<ApplicationResponse> applicants = applicationService.getApplicantsForJob(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Applicants for job", applicants));
    }

    /**
     * PATCH /api/recruiter/applications/{id}/status
     * Body: { status: "SHORTLISTED" }
     * Response: 200 + updated ApplicationResponse
     */
    @PatchMapping("/applications/{id}/status")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        ApplicationResponse application = applicationService.updateApplicationStatus(id, userId, request);
        return ResponseEntity.ok(ApiResponse.success("Application status updated", application));
    }
}
