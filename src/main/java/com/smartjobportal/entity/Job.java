package com.smartjobportal.entity;

import com.smartjobportal.enums.JobStatus;
import com.smartjobportal.enums.JobType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs", indexes = {
        @Index(name = "idx_job_status", columnList = "status"),
        @Index(name = "idx_job_recruiter", columnList = "recruiter_profile_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_profile_id", nullable = false)
    private RecruiterProfile recruiterProfile;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", length = 20)
    @Builder.Default
    private JobType jobType = JobType.FULL_TIME;

    @Column(name = "salary_range", length = 50)
    private String salaryRange;

    @Column(name = "years_experience_required")
    @Builder.Default
    private Integer yearsExperienceRequired = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private JobStatus status = JobStatus.ACTIVE;

    @CreatedDate
    @Column(name = "posted_at", nullable = false, updatable = false)
    private LocalDateTime postedAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<JobSkill> jobSkills = new ArrayList<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    @Builder.Default
    private List<SavedJob> savedJobs = new ArrayList<>();
}
