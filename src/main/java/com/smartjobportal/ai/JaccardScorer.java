package com.smartjobportal.ai;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Computes skill match score using the formula:
 *
 *   score = |matched| / |required| * 100
 *
 * We deliberately use required-skills as the denominator (not union)
 * so a seeker with extra skills is not penalised, and the score
 * clearly expresses "how much of this job can you cover?".
 */
@Component
public class JaccardScorer {

    /**
     * @param seekerSkills   normalized seeker skill set
     * @param requiredSkills normalized required job skill set
     * @return MatchResult with score, matched skills and missing skills
     */
    public ScoreDetail score(Set<String> seekerSkills, Set<String> requiredSkills) {

        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return ScoreDetail.builder()
                    .score(100.0)
                    .matchedSkills(new HashSet<>())
                    .missingSkills(new HashSet<>())
                    .totalRequired(0)
                    .matchedCount(0)
                    .build();
        }

        Set<String> matched = new HashSet<>(seekerSkills);
        matched.retainAll(requiredSkills);

        Set<String> missing = new HashSet<>(requiredSkills);
        missing.removeAll(seekerSkills);

        double score = ((double) matched.size() / requiredSkills.size()) * 100.0;

        return ScoreDetail.builder()
                .score(Math.round(score * 100.0) / 100.0)   // round to 2 dp
                .matchedSkills(matched)
                .missingSkills(missing)
                .totalRequired(requiredSkills.size())
                .matchedCount(matched.size())
                .build();
    }

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ScoreDetail {
        private double score;
        private Set<String> matchedSkills;
        private Set<String> missingSkills;
        private int totalRequired;
        private int matchedCount;
    }
}
