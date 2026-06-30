package com.smartjobportal.repository;

import com.smartjobportal.entity.Application;
import com.smartjobportal.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findBySeekerProfileId(Long seekerProfileId);

    List<Application> findByJobId(Long jobId);

    Optional<Application> findByJobIdAndSeekerProfileId(Long jobId, Long seekerProfileId);

    boolean existsByJobIdAndSeekerProfileId(Long jobId, Long seekerProfileId);

    Long countByStatus(ApplicationStatus status);

    Long countBySeekerProfileId(Long seekerProfileId);

    List<Application> findByJobRecruiterProfileId(Long recruiterProfileId);
}
