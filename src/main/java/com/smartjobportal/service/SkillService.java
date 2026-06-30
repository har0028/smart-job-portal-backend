package com.smartjobportal.service;

import com.smartjobportal.dto.request.SkillRequest;
import com.smartjobportal.dto.response.SkillResponse;

import java.util.List;

public interface SkillService {
    List<SkillResponse> getAllSkills();
    List<SkillResponse> searchSkills(String keyword);
    SkillResponse createSkill(SkillRequest request);
}
