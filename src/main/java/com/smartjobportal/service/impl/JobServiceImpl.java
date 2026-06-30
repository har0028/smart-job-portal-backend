package com.smartjobportal.service.impl;

import com.smartjobportal.dto.request.JobRequest;
import com.smartjobportal.dto.response.JobResponse;
import com.smartjobportal.dto.response.SkillResponse;
import com.smartjobportal.entity.*;
import com.smartjobportal.exception.ResourceNotFoundException;
import com.smartjobportal.exception.UnauthorizedException;
import com.smartjobportal.repository.*;
import com.smartjobportal.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobSkillRepository jobSkillRepository;
    private final SkillRepository skillRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    @Override
    @Transactional
    public JobResponse createJob(JobRequest request, Long userId) {
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));

        Job job = Job.builder()
                .recruiterProfile(recruiterProfile)
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .jobType(request.getJobType())
                .salaryRange(request.getSalaryRange())
                .yearsExperienceRequired(request.getYearsExperienceRequired())
                .status(request.getStatus())
                .expiresAt(request.getExpiresAt())
                .build();

        job = jobRepository.save(job);
        attachSkills(job, request.getSkillIds(), true);
        if (request.getOptionalSkillIds() != null) {
            attachSkills(job, request.getOptionalSkillIds(), false);
        }

        log.info("Job created: '{}' by recruiter userId={}", job.getTitle(), userId);
        return mapToResponse(jobRepository.findById(job.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long jobId, JobRequest request, Long userId) {
        Job job = getJobOwnedByUser(jobId, userId);

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setSalaryRange(request.getSalaryRange());
        job.setYearsExperienceRequired(request.getYearsExperienceRequired());
        job.setStatus(request.getStatus());
        job.setExpiresAt(request.getExpiresAt());

        // Replace skills
        jobSkillRepository.deleteByJobId(jobId);
        attachSkills(job, request.getSkillIds(), true);
        if (request.getOptionalSkillIds() != null) {
            attachSkills(job, request.getOptionalSkillIds(), false);
        }

        job = jobRepository.save(job);
        log.info("Job updated: '{}' by recruiter userId={}", job.getTitle(), userId);
        return mapToResponse(jobRepository.findById(job.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId, Long userId) {
        Job job = getJobOwnedByUser(jobId, userId);
        jobRepository.delete(job);
        log.info("Job deleted: '{}' by recruiter userId={}", job.getTitle(), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));
        return mapToResponse(job);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobResponse> searchJobs(String keyword, String location, String jobType, Pageable pageable) {
        return jobRepository.searchJobs(keyword, location, jobType, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getRecruiterJobs(Long userId) {
        RecruiterProfile profile = recruiterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));
        return jobRepository.findByRecruiterProfileId(profile.getId()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Job getJobOwnedByUser(Long jobId, Long userId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));
        if (!job.getRecruiterProfile().getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this job posting");
        }
        return job;
    }

    private void attachSkills(Job job, List<Long> skillIds, boolean required) {
        if (skillIds == null) return;
        List<JobSkill> jobSkills = new ArrayList<>();
        for (Long skillId : skillIds) {
            Skill skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> new ResourceNotFoundException("Skill", skillId));
            jobSkills.add(JobSkill.builder()
                    .job(job)
                    .skill(skill)
                    .isRequired(required)
                    .build());
        }
        jobSkillRepository.saveAll(jobSkills);
    }

    public JobResponse mapToResponse(Job job) {
        List<SkillResponse> requiredSkills = job.getJobSkills().stream()
                .filter(js -> Boolean.TRUE.equals(js.getIsRequired()))
                .map(js -> SkillResponse.builder()
                        .id(js.getSkill().getId())
                        .name(js.getSkill().getName())
                        .category(js.getSkill().getCategory())
                        .build())
                .toList();

        List<SkillResponse> optionalSkills = job.getJobSkills().stream()
                .filter(js -> Boolean.FALSE.equals(js.getIsRequired()))
                .map(js -> SkillResponse.builder()
                        .id(js.getSkill().getId())
                        .name(js.getSkill().getName())
                        .category(js.getSkill().getCategory())
                        .build())
                .toList();

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
                .requiredSkills(requiredSkills)
                .optionalSkills(optionalSkills)
                .applicationCount(job.getApplications().size())
                .build();
    }
}
