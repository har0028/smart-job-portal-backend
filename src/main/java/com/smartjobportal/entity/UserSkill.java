package com.smartjobportal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_skills",
        uniqueConstraints = @UniqueConstraint(columnNames = {"seeker_profile_id", "skill_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seeker_profile_id", nullable = false)
    private SeekerProfile seekerProfile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "proficiency_level")
    @Builder.Default
    private Integer proficiencyLevel = 1;
}
