package com.smartjobportal.service;

import com.smartjobportal.dto.response.JobResponse;

import java.util.List;

public interface SavedJobService {
    void saveJob(Long jobId, Long userId);
    void unsaveJob(Long jobId, Long userId);
    List<JobResponse> getMySavedJobs(Long userId);
}
