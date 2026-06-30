package com.smartjobportal.controller;

import com.smartjobportal.dto.response.*;
import com.smartjobportal.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    /**
     * GET /api/admin/dashboard
     * Response: platform statistics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboard() {
        return ResponseEntity.ok(
                ApiResponse.success("Dashboard statistics", adminService.getDashboardStats()));
    }

    /**
     * GET /api/admin/users
     * Response: list of all users
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(
                ApiResponse.success("All users", adminService.getAllUsers()));
    }

    /**
     * PATCH /api/admin/users/{id}/block
     * Response: updated user (toggled block status)
     */
    @PatchMapping("/users/{id}/block")
    public ResponseEntity<ApiResponse<UserResponse>> toggleBlockUser(@PathVariable Long id) {
        UserResponse user = adminService.toggleBlockUser(id);
        String msg = Boolean.TRUE.equals(user.getIsActive()) ? "User unblocked" : "User blocked";
        return ResponseEntity.ok(ApiResponse.success(msg, user));
    }

    /**
     * DELETE /api/admin/users/{id}
     * Response: 200 success message
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    /**
     * GET /api/admin/recruiters
     * Response: list of all recruiter profiles
     */
    @GetMapping("/recruiters")
    public ResponseEntity<ApiResponse<List<RecruiterProfileResponse>>> getAllRecruiters() {
        return ResponseEntity.ok(
                ApiResponse.success("All recruiters", adminService.getAllRecruiters()));
    }

    /**
     * GET /api/admin/jobs
     * Response: list of all jobs across platform
     */
    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getAllJobs() {
        return ResponseEntity.ok(
                ApiResponse.success("All jobs", adminService.getAllJobs()));
    }

    /**
     * DELETE /api/admin/jobs/{id}
     * Response: 200 success message
     */
    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id) {
        adminService.deleteJob(id);
        return ResponseEntity.ok(ApiResponse.success("Job deleted successfully"));
    }
}
