package com.smartjobportal.repository;

import com.smartjobportal.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    List<UserSkill> findBySeekerProfileId(Long seekerProfileId);

    Optional<UserSkill> findBySeekerProfileIdAndSkillId(Long seekerProfileId, Long skillId);

    boolean existsBySeekerProfileIdAndSkillId(Long seekerProfileId, Long skillId);

    void deleteBySeekerProfileIdAndSkillId(Long seekerProfileId, Long skillId);
}
