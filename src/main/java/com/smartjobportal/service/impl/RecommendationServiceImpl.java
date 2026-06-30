package com.smartjobportal.service.impl;

import com.smartjobportal.ai.JaccardScorer;
import com.smartjobportal.ai.MatchResult;
import com.smartjobportal.ai.RecommendationExplainer;
import com.smartjobportal.ai.SkillNormalizer;
import com.smartjobportal.dto.response.RecommendationResponse;
import com.smartjobportal.entity.*;
import com.smartjobportal.exception.ResourceNotFoundException;
import com.smartjobportal.repository.*;
import com.smartjobportal.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final SeekerProfileRepository seekerProfileRepository;
    private final JobRepository jobRepository;
    private final SkillNormalizer skillNormalizer;
    private final JaccardScorer jaccardScorer;
    private final RecommendationExplainer explainer;

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationResponse> getRecommendations(Long userId) {
        SeekerProfile seeker = seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile not found"));

        // Get normalized seeker skill set
        Set<String> seekerSkills = extractNormalizedSkills(seeker);

        // Get all active jobs not already applied to
        List<Job> candidateJobs = jobRepository.findActiveJobsNotAppliedBySeeker(seeker.getId());

        if (candidateJobs.isEmpty()) {
            log.info("No candidate jobs found for userId={}", userId);
            return Collections.emptyList();
        }

        // Score and rank each job
        List<MatchResult> results = candidateJobs.stream()
                .map(job -> computeMatchResult(seekerSkills, seeker, job))
                .sorted(Comparator.comparingDouble(MatchResult::getFinalScore).reversed())
                .toList();

        // Build ranked response
        AtomicInteger rank = new AtomicInteger(1);
        List<RecommendationResponse> recommendations = results.stream()
                .map(result -> buildResponse(result, rank.getAndIncrement()))
                .toList();

        log.info("Generated {} recommendations for userId={}", recommendations.size(), userId);
        return recommendations;
    }

    @Override
    @Transactional(readOnly = true)
    public RecommendationResponse getScoreForJob(Long userId, Long jobId) {
        SeekerProfile seeker = seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));

        Set<String> seekerSkills = extractNormalizedSkills(seeker);
        MatchResult result = computeMatchResult(seekerSkills, seeker, job);
        return buildResponse(result, 1);
    }

    // ── Core Engine ───────────────────────────────────────────────────────────

    /**
     * Computes a composite weighted score for one job:
     *
     *   finalScore = (skillScore × 0.70)
     *              + (experienceScore × 0.20)
     *              + (recencyScore × 0.10)
     *
     * Skill match is intentionally weighted highest because it is the
     * strongest signal for job fit at screening stage.
     */
    private MatchResult computeMatchResult(Set<String> seekerSkills,
                                           SeekerProfile seeker,
                                           Job job) {
        // ── 1. Skill match (Jaccard over required skills) ─────────────────────
        Set<String> requiredSkills = job.getJobSkills().stream()
                .filter(js -> Boolean.TRUE.equals(js.getIsRequired()))
                .map(js -> js.getSkill().getName())
                .collect(Collectors.toSet());

        Set<String> normalizedRequired = skillNormalizer.normalizeAll(requiredSkills);
        JaccardScorer.ScoreDetail scoreDetail = jaccardScorer.score(seekerSkills, normalizedRequired);
        double skillScore = scoreDetail.getScore();

        // ── 2. Experience score (linear proximity) ────────────────────────────
        double experienceScore = computeExperienceScore(
                seeker.getYearsExperience(),
                job.getYearsExperienceRequired()
        );

        // ── 3. Recency score (decay over 30 days) ─────────────────────────────
        double recencyScore = computeRecencyScore(job);

        // ── 4. Weighted composite ─────────────────────────────────────────────
        double finalScore = (skillScore * 0.70)
                + (experienceScore * 0.20)
                + (recencyScore * 0.10);

        finalScore = Math.round(finalScore * 100.0) / 100.0;

        // ── 5. Build MatchResult ──────────────────────────────────────────────
        MatchResult result = MatchResult.builder()
                .jobId(job.getId())
                .jobTitle(job.getTitle())
                .companyName(job.getRecruiterProfile().getCompanyName())
                .location(job.getLocation())
                .jobType(job.getJobType() != null ? job.getJobType().name() : "")
                .salaryRange(job.getSalaryRange())
                .skillMatchScore(skillScore)
                .finalScore(finalScore)
                .matchedSkills(scoreDetail.getMatchedSkills())
                .missingSkills(scoreDetail.getMissingSkills())
                .totalRequiredSkills(scoreDetail.getTotalRequired())
                .matchedCount(scoreDetail.getMatchedCount())
                .build();

        return result;
    }

    /**
     * Penalises under-experienced candidates linearly.
     * Over-qualified candidates are not penalised.
     *
     *   score = 100  if seeker >= required
     *           max(0, 100 - (gap * 15))  otherwise
     */
    private double computeExperienceScore(int seekerYears, int requiredYears) {
        if (requiredYears <= 0) return 100.0;
        if (seekerYears >= requiredYears) return 100.0;
        int gap = requiredYears - seekerYears;
        return Math.max(0.0, 100.0 - (gap * 15.0));
    }

    /**
     * Jobs posted within 7 days score 100.
     * Score decays linearly to 0 at 30 days.
     */
    private double computeRecencyScore(Job job) {
        if (job.getPostedAt() == null) return 50.0;
        long daysOld = java.time.temporal.ChronoUnit.DAYS.between(
                job.getPostedAt(), java.time.LocalDateTime.now());
        if (daysOld <= 7) return 100.0;
        if (daysOld >= 30) return 0.0;
        return Math.max(0.0, 100.0 - ((daysOld - 7) * (100.0 / 23.0)));
    }

    private Set<String> extractNormalizedSkills(SeekerProfile seeker) {
        Set<String> raw = seeker.getUserSkills().stream()
                .map(us -> us.getSkill().getName())
                .collect(Collectors.toSet());
        return skillNormalizer.normalizeAll(raw);
    }

    private RecommendationResponse buildResponse(MatchResult result, int rank) {
        String reason = explainer.explain(result);
        return RecommendationResponse.builder()
                .jobId(result.getJobId())
                .jobTitle(result.getJobTitle())
                .companyName(result.getCompanyName())
                .location(result.getLocation())
                .jobType(result.getJobType())
                .salaryRange(result.getSalaryRange())
                .matchPercentage(result.getSkillMatchScore())
                .matchedSkills(new ArrayList<>(result.getMatchedSkills()))
                .missingSkills(new ArrayList<>(result.getMissingSkills()))
                .recommendationReason(reason)
                .rank(rank)
                .build();
    }
}
