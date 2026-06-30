package com.smartjobportal.service;

import com.smartjobportal.dto.request.JobRequest;
import com.smartjobportal.dto.response.JobResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobService {
    JobResponse createJob(JobRequest request, Long userId);
    JobResponse updateJob(Long jobId, JobRequest request, Long userId);
    void deleteJob(Long jobId, Long userId);
    JobResponse getJobById(Long jobId);
    Page<JobResponse> searchJobs(String keyword, String location, String jobType, Pageable pageable);
    List<JobResponse> getRecruiterJobs(Long userId);
}
