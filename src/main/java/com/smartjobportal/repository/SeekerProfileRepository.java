package com.smartjobportal.repository;

import com.smartjobportal.entity.SeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeekerProfileRepository extends JpaRepository<SeekerProfile, Long> {

    Optional<SeekerProfile> findByUserId(Long userId);

    @Query("SELECT sp FROM SeekerProfile sp JOIN FETCH sp.userSkills us JOIN FETCH us.skill WHERE sp.user.id = :userId")
    Optional<SeekerProfile> findByUserIdWithSkills(Long userId);
}
