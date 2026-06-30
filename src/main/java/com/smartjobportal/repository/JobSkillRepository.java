package com.smartjobportal.repository;

import com.smartjobportal.entity.JobSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSkillRepository extends JpaRepository<JobSkill, Long> {

    List<JobSkill> findByJobId(Long jobId);

    List<JobSkill> findByJobIdAndIsRequired(Long jobId, Boolean isRequired);

    void deleteByJobId(Long jobId);
}
