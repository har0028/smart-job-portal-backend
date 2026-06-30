package com.smartjobportal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recruiter_profiles")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    @Column(name = "company_website", length = 200)
    private String companyWebsite;

    @Column(length = 100)
    private String designation;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "recruiterProfile", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Job> jobs = new ArrayList<>();
}
