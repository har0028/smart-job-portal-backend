package com.smartjobportal.service.impl;

import com.smartjobportal.dto.request.ProfileRequest;
import com.smartjobportal.dto.request.UserSkillRequest;
import com.smartjobportal.dto.response.SeekerProfileResponse;
import com.smartjobportal.dto.response.UserSkillResponse;
import com.smartjobportal.entity.*;
import com.smartjobportal.exception.BadRequestException;
import com.smartjobportal.exception.ResourceNotFoundException;
import com.smartjobportal.repository.*;
import com.smartjobportal.service.SeekerService;
import com.smartjobportal.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeekerServiceImpl implements SeekerService {

    private final SeekerProfileRepository seekerProfileRepository;
    private final UserSkillRepository userSkillRepository;
    private final SkillRepository skillRepository;
    private final FileStorageUtil fileStorageUtil;

    @Override
    @Transactional(readOnly = true)
    public SeekerProfileResponse getProfile(Long userId) {
        SeekerProfile profile = seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile not found for user: " + userId));
        return mapToResponse(profile);
    }

    @Override
    @Transactional
    public SeekerProfileResponse updateProfile(Long userId, ProfileRequest request) {
        SeekerProfile profile = seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile not found for user: " + userId));

        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getYearsExperience() != null) profile.setYearsExperience(request.getYearsExperience());

        profile = seekerProfileRepository.save(profile);
        log.info("Seeker profile updated for userId={}", userId);
        return mapToResponse(profile);
    }

    @Override
    @Transactional
    public String uploadResume(Long userId, MultipartFile file) {
        SeekerProfile profile = seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile not found for user: " + userId));

        // Delete old resume if exists
        if (profile.getResumeUrl() != null) {
            fileStorageUtil.deleteFile(profile.getResumeUrl());
        }

        String filePath = fileStorageUtil.storeResume(file, userId);
        profile.setResumeUrl(filePath);
        seekerProfileRepository.save(profile);

        log.info("Resume uploaded for userId={}: {}", userId, filePath);
        return filePath;
    }

    @Override
    @Transactional
    public UserSkillResponse addSkill(Long userId, UserSkillRequest request) {
        SeekerProfile profile = seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile not found for user: " + userId));

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill", request.getSkillId()));

        if (userSkillRepository.existsBySeekerProfileIdAndSkillId(profile.getId(), skill.getId())) {
            throw new BadRequestException("Skill already added: " + skill.getName());
        }

        UserSkill userSkill = UserSkill.builder()
                .seekerProfile(profile)
                .skill(skill)
                .proficiencyLevel(request.getProficiencyLevel())
                .build();

        userSkill = userSkillRepository.save(userSkill);
        log.info("Skill '{}' added to seekerProfileId={}", skill.getName(), profile.getId());
        return mapSkillToResponse(userSkill);
    }

    @Override
    @Transactional
    public void removeSkill(Long userId, Long skillId) {
        SeekerProfile profile = seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile not found for user: " + userId));

        if (!userSkillRepository.existsBySeekerProfileIdAndSkillId(profile.getId(), skillId)) {
            throw new ResourceNotFoundException("Skill not found in your profile");
        }

        userSkillRepository.deleteBySeekerProfileIdAndSkillId(profile.getId(), skillId);
        log.info("Skill {} removed from seekerProfileId={}", skillId, profile.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSkillResponse> getMySkills(Long userId) {
        SeekerProfile profile = seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile not found for user: " + userId));
        return userSkillRepository.findBySeekerProfileId(profile.getId()).stream()
                .map(this::mapSkillToResponse)
                .toList();
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private SeekerProfileResponse mapToResponse(SeekerProfile profile) {
        List<UserSkillResponse> skills = profile.getUserSkills().stream()
                .map(this::mapSkillToResponse)
                .toList();

        return SeekerProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .fullName(profile.getUser().getFullName())
                .email(profile.getUser().getEmail())
                .phone(profile.getPhone())
                .location(profile.getLocation())
                .bio(profile.getBio())
                .resumeUrl(profile.getResumeUrl())
                .yearsExperience(profile.getYearsExperience())
                .skills(skills)
                .build();
    }

    private UserSkillResponse mapSkillToResponse(UserSkill us) {
        return UserSkillResponse.builder()
                .id(us.getId())
                .skillId(us.getSkill().getId())
                .skillName(us.getSkill().getName())
                .category(us.getSkill().getCategory())
                .proficiencyLevel(us.getProficiencyLevel())
                .build();
    }
}
