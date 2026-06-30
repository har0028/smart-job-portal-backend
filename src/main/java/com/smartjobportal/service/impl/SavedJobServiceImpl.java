package com.smartjobportal.service.impl;

import com.smartjobportal.dto.response.JobResponse;
import com.smartjobportal.entity.*;
import com.smartjobportal.exception.BadRequestException;
import com.smartjobportal.exception.ResourceNotFoundException;
import com.smartjobportal.repository.*;
import com.smartjobportal.service.SavedJobService;
import com.smartjobportal.service.impl.JobServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavedJobServiceImpl implements SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobServiceImpl jobServiceImpl;

    @Override
    @Transactional
    public void saveJob(Long jobId, Long userId) {
        if (savedJobRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new BadRequestException("Job is already saved");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));

        SavedJob savedJob = SavedJob.builder()
                .user(user)
                .job(job)
                .build();

        savedJobRepository.save(savedJob);
        log.info("Job {} saved by userId={}", jobId, userId);
    }

    @Override
    @Transactional
    public void unsaveJob(Long jobId, Long userId) {
        if (!savedJobRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new ResourceNotFoundException("Saved job not found");
        }
        savedJobRepository.deleteByUserIdAndJobId(userId, jobId);
        log.info("Job {} unsaved by userId={}", jobId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getMySavedJobs(Long userId) {
        return savedJobRepository.findByUserId(userId).stream()
                .map(sj -> jobServiceImpl.mapToResponse(sj.getJob()))
                .toList();
    }
}
