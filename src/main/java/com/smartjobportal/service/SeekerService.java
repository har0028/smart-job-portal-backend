package com.smartjobportal.service;

import com.smartjobportal.dto.request.ProfileRequest;
import com.smartjobportal.dto.request.UserSkillRequest;
import com.smartjobportal.dto.response.SeekerProfileResponse;
import com.smartjobportal.dto.response.UserSkillResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SeekerService {
    SeekerProfileResponse getProfile(Long userId);
    SeekerProfileResponse updateProfile(Long userId, ProfileRequest request);
    String uploadResume(Long userId, MultipartFile file);
    UserSkillResponse addSkill(Long userId, UserSkillRequest request);
    void removeSkill(Long userId, Long skillId);
    List<UserSkillResponse> getMySkills(Long userId);
}
