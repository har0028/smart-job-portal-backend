package com.smartjobportal.service.impl;

import com.smartjobportal.ai.JaccardScorer;
import com.smartjobportal.ai.SkillNormalizer;
import com.smartjobportal.dto.request.ApplicationRequest;
import com.smartjobportal.dto.request.ApplicationStatusRequest;
import com.smartjobportal.dto.response.ApplicationResponse;
import com.smartjobportal.entity.*;
import com.smartjobportal.enums.JobStatus;
import com.smartjobportal.exception.BadRequestException;
import com.smartjobportal.exception.ResourceNotFoundException;
import com.smartjobportal.exception.UnauthorizedException;
import com.smartjobportal.repository.*;
import com.smartjobportal.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final SeekerProfileRepository seekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final SkillNormalizer skillNormalizer;
    private final JaccardScorer jaccardScorer;

    @Override
    @Transactional
    public ApplicationResponse applyForJob(Long jobId, Long userId, ApplicationRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));

        if (job.getStatus() != JobStatus.ACTIVE) {
            throw new BadRequestException("This job is no longer accepting applications");
        }

        SeekerProfile seekerProfile = seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile not found"));

        if (applicationRepository.existsByJobIdAndSeekerProfileId(jobId, seekerProfile.getId())) {
            throw new BadRequestException("You have already applied for this job");
        }

        // Calculate match score at application time (frozen snapshot)
        double matchScore = computeMatchScore(seekerProfile, job);

        Application application = Application.builder()
                .job(job)
                .seekerProfile(seekerProfile)
                .coverLetter(request.getCoverLetter())
                .matchScore(matchScore)
                .build();

        application = applicationRepository.save(application);
        log.info("Application submitted: jobId={} seekerProfileId={} matchScore={}%",
                jobId, seekerProfile.getId(), matchScore);

        return mapToResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications(Long userId) {
        SeekerProfile profile = seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile not found"));
        return applicationRepository.findBySeekerProfileId(profile.getId()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicantsForJob(Long jobId, Long recruiterId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));

        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));

        if (!job.getRecruiterProfile().getId().equals(recruiterProfile.getId())) {
            throw new UnauthorizedException("You do not own this job posting");
        }

        return applicationRepository.findByJobId(jobId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplicationStatus(Long applicationId, Long recruiterId,
                                                       ApplicationStatusRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));

        if (!application.getJob().getRecruiterProfile().getId().equals(recruiterProfile.getId())) {
            throw new UnauthorizedException("You do not own this job posting");
        }

        application.setStatus(request.getStatus());
        application = applicationRepository.save(application);
        log.info("Application {} status updated to {} by recruiter {}",
                applicationId, request.getStatus(), recruiterId);

        return mapToResponse(application);
    }

    @Override
    @Transactional
    public void withdrawApplication(Long applicationId, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        SeekerProfile profile = seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile not found"));

        if (!application.getSeekerProfile().getId().equals(profile.getId())) {
            throw new UnauthorizedException("You did not submit this application");
        }

        applicationRepository.delete(application);
        log.info("Application {} withdrawn by userId={}", applicationId, userId);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private double computeMatchScore(SeekerProfile seeker, Job job) {
        Set<String> seekerSkills = seeker.getUserSkills().stream()
                .map(us -> us.getSkill().getName())
                .collect(Collectors.toSet());

        Set<String> requiredSkills = job.getJobSkills().stream()
                .filter(js -> Boolean.TRUE.equals(js.getIsRequired()))
                .map(js -> js.getSkill().getName())
                .collect(Collectors.toSet());

        Set<String> normalizedSeeker = skillNormalizer.normalizeAll(seekerSkills);
        Set<String> normalizedRequired = skillNormalizer.normalizeAll(requiredSkills);

        return jaccardScorer.score(normalizedSeeker, normalizedRequired).getScore();
    }

    private ApplicationResponse mapToResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .companyName(application.getJob().getRecruiterProfile().getCompanyName())
                .seekerId(application.getSeekerProfile().getId())
                .seekerName(application.getSeekerProfile().getUser().getFullName())
                .seekerEmail(application.getSeekerProfile().getUser().getEmail())
                .status(application.getStatus())
                .coverLetter(application.getCoverLetter())
                .matchScore(application.getMatchScore())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}
