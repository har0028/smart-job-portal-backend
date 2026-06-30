package com.smartjobportal.service;

import com.smartjobportal.dto.response.RecommendationResponse;

import java.util.List;

public interface RecommendationService {
    List<RecommendationResponse> getRecommendations(Long userId);
    RecommendationResponse getScoreForJob(Long userId, Long jobId);
}
