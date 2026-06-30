package com.smartjobportal.service;

import com.smartjobportal.dto.request.ApplicationRequest;
import com.smartjobportal.dto.request.ApplicationStatusRequest;
import com.smartjobportal.dto.response.ApplicationResponse;

import java.util.List;

public interface ApplicationService {
    ApplicationResponse applyForJob(Long jobId, Long userId, ApplicationRequest request);
    List<ApplicationResponse> getMyApplications(Long userId);
    List<ApplicationResponse> getApplicantsForJob(Long jobId, Long recruiterId);
    ApplicationResponse updateApplicationStatus(Long applicationId, Long recruiterId, ApplicationStatusRequest request);
    void withdrawApplication(Long applicationId, Long userId);
}
