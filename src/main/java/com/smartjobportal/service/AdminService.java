package com.smartjobportal.service;

import com.smartjobportal.dto.response.DashboardStatsResponse;
import com.smartjobportal.dto.response.JobResponse;
import com.smartjobportal.dto.response.RecruiterProfileResponse;
import com.smartjobportal.dto.response.UserResponse;

import java.util.List;

public interface AdminService {
    DashboardStatsResponse getDashboardStats();
    List<UserResponse> getAllUsers();
    UserResponse toggleBlockUser(Long userId);
    void deleteUser(Long userId);
    List<RecruiterProfileResponse> getAllRecruiters();
    List<JobResponse> getAllJobs();
    void deleteJob(Long jobId);
}
