package com.smartjobportal.repository;

import com.smartjobportal.entity.Job;
import com.smartjobportal.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByRecruiterProfileId(Long recruiterProfileId);

    List<Job> findByStatus(JobStatus status);

    Long countByStatus(JobStatus status);

    @Query("""
            SELECT j FROM Job j
            WHERE j.status = 'ACTIVE'
            AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
            AND (:jobType IS NULL OR CAST(j.jobType AS string) = :jobType)
            """)
    Page<Job> searchJobs(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("jobType") String jobType,
            Pageable pageable
    );

    @Query("""
            SELECT j FROM Job j
            WHERE j.status = 'ACTIVE'
            AND j.id NOT IN (
                SELECT a.job.id FROM Application a WHERE a.seekerProfile.id = :seekerProfileId
            )
            """)
    List<Job> findActiveJobsNotAppliedBySeeker(@Param("seekerProfileId") Long seekerProfileId);
}
