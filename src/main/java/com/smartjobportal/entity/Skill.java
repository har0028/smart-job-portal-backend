package com.smartjobportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skills", indexes = {
        @Index(name = "idx_skill_name", columnList = "name", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 50)
    private String category;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserSkill> userSkills = new ArrayList<>();

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
    @Builder.Default
    private List<JobSkill> jobSkills = new ArrayList<>();
}
