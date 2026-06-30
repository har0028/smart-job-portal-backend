package com.smartjobportal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seeker_profiles")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeekerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(name = "years_experience")
    @Builder.Default
    private Integer yearsExperience = 0;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "seekerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserSkill> userSkills = new ArrayList<>();

    @OneToMany(mappedBy = "seekerProfile", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();
}
