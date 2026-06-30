package com.smartjobportal.service.impl;

import com.smartjobportal.dto.response.*;
import com.smartjobportal.entity.Job;
import com.smartjobportal.entity.RecruiterProfile;
import com.smartjobportal.entity.User;
import com.smartjobportal.enums.ApplicationStatus;
import com.smartjobportal.enums.JobStatus;
import com.smartjobportal.enums.Role;
import com.smartjobportal.exception.BadRequestException;
import com.smartjobportal.exception.ResourceNotFoundException;
import com.smartjobportal.repository.*;
import com.smartjobportal.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        return DashboardStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalRecruiters(userRepository.countByRole(Role.RECRUITER))
                .totalJobSeekers(userRepository.countByRole(Role.JOB_SEEKER))
                .totalJobs(jobRepository.count())
                .activeJobs(jobRepository.countByStatus(JobStatus.ACTIVE))
                .totalApplications(applicationRepository.count())
                .pendingApplications(applicationRepository.countByStatus(ApplicationStatus.PENDING))
                .blockedUsers(userRepository.countBlockedUsers())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse toggleBlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (user.getRole() == Role.ADMIN) {
            throw new BadRequestException("Cannot block an admin account");
        }

        user.setIsActive(!user.getIsActive());
        user = userRepository.save(user);

        String action = user.getIsActive() ? "unblocked" : "blocked";
        log.info("Admin {} user: {}", action, user.getEmail());

        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (user.getRole() == Role.ADMIN) {
            throw new BadRequestException("Cannot delete an admin account");
        }

        userRepository.delete(user);
        log.info("Admin deleted user: {}", user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecruiterProfileResponse> getAllRecruiters() {
        return recruiterProfileRepository.findAll().stream()
                .map(this::mapToRecruiterResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::mapToJobResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));
        jobRepository.delete(job);
        log.info("Admin deleted job: {}", job.getTitle());
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private RecruiterProfileResponse mapToRecruiterResponse(RecruiterProfile rp) {
        return RecruiterProfileResponse.builder()
                .id(rp.getId())
                .userId(rp.getUser().getId())
                .fullName(rp.getUser().getFullName())
                .email(rp.getUser().getEmail())
                .companyName(rp.getCompanyName())
                .companyWebsite(rp.getCompanyWebsite())
                .designation(rp.getDesignation())
                .totalJobs(rp.getJobs().size())
                .build();
    }

    private JobResponse mapToJobResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .salaryRange(job.getSalaryRange())
                .yearsExperienceRequired(job.getYearsExperienceRequired())
                .status(job.getStatus())
                .postedAt(job.getPostedAt())
                .expiresAt(job.getExpiresAt())
                .companyName(job.getRecruiterProfile().getCompanyName())
                .recruiterName(job.getRecruiterProfile().getUser().getFullName())
                .recruiterId(job.getRecruiterProfile().getId())
                .applicationCount(job.getApplications().size())
                .build();
    }
}
