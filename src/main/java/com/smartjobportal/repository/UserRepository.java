package com.smartjobportal.repository;

import com.smartjobportal.entity.User;
import com.smartjobportal.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByIsActive(Boolean isActive);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(Role role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = false")
    Long countBlockedUsers();
}
