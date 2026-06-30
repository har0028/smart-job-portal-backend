package com.smartjobportal.service.impl;

import com.smartjobportal.dto.request.SkillRequest;
import com.smartjobportal.dto.response.SkillResponse;
import com.smartjobportal.entity.Skill;
import com.smartjobportal.exception.BadRequestException;
import com.smartjobportal.repository.SkillRepository;
import com.smartjobportal.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> searchSkills(String keyword) {
        return skillRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public SkillResponse createSkill(SkillRequest request) {
        if (skillRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Skill already exists: " + request.getName());
        }
        Skill skill = Skill.builder()
                .name(request.getName().trim().toLowerCase())
                .category(request.getCategory())
                .build();
        skill = skillRepository.save(skill);
        return mapToResponse(skill);
    }

    public SkillResponse mapToResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .build();
    }
}
