package com.smartjobportal.controller;

import com.smartjobportal.dto.response.ApiResponse;
import com.smartjobportal.dto.response.RecommendationResponse;
import com.smartjobportal.service.RecommendationService;
import com.smartjobportal.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('JOB_SEEKER')")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final SecurityUtils securityUtils;

    /**
     * GET /api/recommendations
     * Response: ranked list of recommended jobs with match score and reason
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> getRecommendations() {
        Long userId = securityUtils.getCurrentUserId();
        List<RecommendationResponse> recommendations =
                recommendationService.getRecommendations(userId);
        return ResponseEntity.ok(ApiResponse.success(
                "Job recommendations generated (" + recommendations.size() + " results)",
                recommendations));
    }

    /**
     * GET /api/recommendations/{jobId}/score
     * Response: match score and explanation for a specific job
     */
    @GetMapping("/{jobId}/score")
    public ResponseEntity<ApiResponse<RecommendationResponse>> getScoreForJob(
            @PathVariable Long jobId) {
        Long userId = securityUtils.getCurrentUserId();
        RecommendationResponse response = recommendationService.getScoreForJob(userId, jobId);
        return ResponseEntity.ok(ApiResponse.success("Match score calculated", response));
    }
}
